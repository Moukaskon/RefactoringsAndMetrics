"""
beauty_worker.py  –  Standalone Python process for the two-clone pipeline.

Role
----
The Java miner runs in Clone A.  Each time it finishes a 5-commit xlsx batch it
drops a <batch>.ready signal file next to the xlsx.  This process runs in
parallel, watches for those signal files, checks out the matching commits in
Clone B, computes the beauty metrics, and writes them directly into the xlsx
(columns 38-45).  When finished it renames the signal to <batch>.done.

Usage
-----
    # Fresh run (process everything)
    python beauty_worker.py \\
        --watch-dir  /path/to/XLSXs/<project>   \\
        --clone-b    /path/to/Allprojects/<project>_beauty  \\
        --workers    8

    # Resume – skip batches whose batchStart < 1381 (match Java: java -jar app.jar <url> 1381)
    python beauty_worker.py \\
        --watch-dir  /path/to/XLSXs/<project>   \\
        --clone-b    /path/to/Allprojects/<project>_beauty  \\
        --workers    8                                       \\
        --start-from-commit 1381

Arguments
---------
--watch-dir          Directory that contains the xlsx files AND the .ready
                     signals. Corresponds to XLSXs/<projectName>/ inside the
                     container.
--clone-b            Absolute path to the SECOND git clone used exclusively by
                     this worker for checkouts. Must already be cloned before
                     starting.
--workers            Thread-pool size for per-file beauty computation
                     (default: 8).
--poll               Polling interval in seconds (default: 2).
--start-from-commit  Skip every xlsx batch whose batchStart is strictly less
                     than this number.  Use the same value you passed to Java's
                     startingCommitNumber argument when resuming.  (default: 0,
                     meaning process all batches.)
--git-url            If provided and clone-b does not contain a .git directory,
                     the worker will automatically run
                     `git clone <git-url> <clone-b>` before starting the watch
                     loop.  This makes docker-compose up fully self-contained.
"""

import argparse
import os
import sys
import time
import concurrent.futures
import subprocess

# ── locate sibling modules ───────────────────────────────────────────────────
_HERE = os.path.dirname(os.path.abspath(__file__))
if _HERE not in sys.path:
    sys.path.insert(0, _HERE)

from simplicity   import calculate_simplicity
from symmetry     import calculate_symmetry
from equilibrium  import calculate_equilibrium
from rhythm       import calculate_rhythm
from regularity   import calculate_regularity
from sequence     import calculate_sequence
from density      import calculate_density
from balance      import calculate_balance

# openpyxl is used here because we need to open an EXISTING xlsx and edit
# specific cells – openpyxl handles that cleanly.  POI writes it, openpyxl
# patches it; both speak the same .xlsx format.
try:
    import openpyxl
except ImportError:
    sys.exit("[beauty_worker] ERROR: openpyxl is not installed.  "
             "Run: pip install openpyxl")


# ── Column indices (0-based, matching the Java header row) ───────────────────
COL_SHA      = 1   # "SHA"
COL_FILES    = 3   # "Files"   (relative .java path, one file per row)
COL_BALANCE      = 38
COL_EQUILIBRIUM  = 39
COL_DENSITY      = 40
COL_REGULARITY   = 41
COL_RHYTHM       = 42
COL_SEQUENCE     = 43
COL_SIMPLICITY   = 44
COL_SYMMETRY     = 45


# ── Per-file beauty computation ───────────────────────────────────────────────

def _process_file(file_path: str) -> list[float]:
    """Return [balance, equilibrium, density, regularity, rhythm, sequence,
               simplicity, symmetry] for one .java file."""
    try:
        try:
            with open(file_path, 'r', encoding='utf-8') as fh:
                lines = fh.readlines()
        except UnicodeDecodeError:
            with open(file_path, 'r', encoding='latin1') as fh:
                lines = fh.readlines()

        while lines and not lines[0].strip():
            lines.pop(0)
        while lines and not lines[-1].strip():
            lines.pop()

        if not lines:
            return [0.0] * 8

        num_lines = len(lines)
        max_line_length = max(
            sum(4 if ch == '\t' else 1 for ch in line.rstrip('\n'))
            for line in lines
        )

        table_rows = num_lines
        table_cols = max_line_length
        table = [[' '] * table_cols for _ in range(table_rows)]

        for row_idx, line in enumerate(lines):
            col_idx = 0
            for ch in line.rstrip('\n'):
                if ch == '\t':
                    col_idx += 4
                else:
                    if row_idx < table_rows and col_idx < table_cols:
                        table[row_idx][col_idx] = ch
                    col_idx += 1

        binary = [[1 if cell.strip() else 0 for cell in row] for row in table]

        return [
            calculate_balance(binary),
            calculate_equilibrium(binary),
            calculate_density(binary),
            calculate_regularity(binary),
            calculate_rhythm(binary),
            calculate_sequence(binary),
            calculate_simplicity(binary),
            calculate_symmetry(binary),
        ]

    except Exception as exc:
        print(f"[beauty_worker] WARN: could not process {file_path}: {exc}",
              file=sys.stderr)
        return [0.0] * 8


def _ensure_clone(git_url: str, clone_b: str) -> None:
    """
    Clone *git_url* into *clone_b* if *clone_b* does not already contain a
    .git directory.  Exits the process on failure.
    """
    git_dir = os.path.join(clone_b, ".git")
    if os.path.isdir(git_dir):
        print(f"[beauty_worker] Clone B already exists at: {clone_b}")
        return

    print(f"[beauty_worker] Clone B not found — cloning {git_url} → {clone_b}")
    parent = os.path.dirname(clone_b)
    os.makedirs(parent, exist_ok=True)

    try:
        result = subprocess.run(
            ["git", "clone", git_url, clone_b],
            capture_output=False,   # let git print progress to the terminal
            text=True,
            timeout=1800,           # 30-minute ceiling for very large repos
        )
        if result.returncode != 0:
            sys.exit(f"[beauty_worker] ERROR: git clone failed (exit {result.returncode}).")
    except Exception as exc:
        sys.exit(f"[beauty_worker] ERROR during git clone: {exc}")

    print(f"[beauty_worker] Clone complete: {clone_b}")


def _git_checkout(clone_b: str, sha: str) -> bool:
    """Checkout *sha* in *clone_b*.  Returns True on success."""
    try:
        result = subprocess.run(
            ["git", "checkout", sha],
            cwd=clone_b,
            capture_output=True,
            text=True,
            timeout=120,
        )
        if result.returncode != 0:
            print(f"[beauty_worker] git checkout {sha} failed:\n{result.stderr}",
                  file=sys.stderr)
            return False
        return True
    except Exception as exc:
        print(f"[beauty_worker] git checkout error: {exc}", file=sys.stderr)
        return False


# ── xlsx patch logic ──────────────────────────────────────────────────────────

def _patch_xlsx(xlsx_path: str, clone_b: str, max_workers: int) -> None:
    """
    Open the xlsx written by Java, group rows by SHA, checkout each SHA in
    Clone B, compute beauty metrics for each file, then write back.

    The xlsx layout (0-based column indices):
        col 1 = SHA
        col 3 = relative file path (one .java file per row)
        col 38-45 = beauty metrics to fill in
    Row 0 is the header – skip it.
    Summary rows (where col 3 is empty/None) are also skipped.
    """
    print(f"[beauty_worker] Patching: {xlsx_path}")
    wb = openpyxl.load_workbook(xlsx_path)
    ws = wb.active

    # ── Group rows by SHA (preserve order so we checkout each SHA once) ──────
    sha_to_rows: dict[str, list] = {}  # sha → list of worksheet Row objects
    for row in ws.iter_rows(min_row=2):           # skip header row
        sha_cell  = row[COL_SHA]
        file_cell = row[COL_FILES]
        if sha_cell.value is None or file_cell.value is None:
            continue                               # summary row, skip
        sha = str(sha_cell.value).strip()
        if sha not in sha_to_rows:
            sha_to_rows[sha] = []
        sha_to_rows[sha].append(row)

    print(f"[beauty_worker] Found {len(sha_to_rows)} unique SHAs to process.")

    for sha, rows in sha_to_rows.items():
        print(f"[beauty_worker]   Checking out {sha[:8]}… ({len(rows)} files)")

        if not _git_checkout(clone_b, sha):
            print(f"[beauty_worker]   SKIP {sha} – checkout failed.")
            continue

        # Build list of (absolute_path, row) pairs
        tasks = []
        for row in rows:
            rel_path = str(row[COL_FILES].value).strip().replace("/", os.sep)
            abs_path = os.path.join(clone_b, rel_path)
            tasks.append((abs_path, row))

        # Parallel beauty computation for all files in this commit
        with concurrent.futures.ThreadPoolExecutor(max_workers=max_workers) as pool:
            future_to_row = {
                pool.submit(_process_file, abs_path): row
                for abs_path, row in tasks
            }
            for future in concurrent.futures.as_completed(future_to_row):
                row    = future_to_row[future]
                metrics = future.result()           # always returns a list of 8

                b, eq, d, reg, rh, seq, si, sy = metrics

                # openpyxl rows are 1-indexed tuples; index by COL_* + 1
                row[COL_BALANCE].value      = b
                row[COL_EQUILIBRIUM].value  = eq
                row[COL_DENSITY].value      = d
                row[COL_REGULARITY].value   = reg
                row[COL_RHYTHM].value       = rh
                row[COL_SEQUENCE].value     = seq
                row[COL_SIMPLICITY].value   = si
                row[COL_SYMMETRY].value     = sy

    # Save the patched workbook back to the same file
    wb.save(xlsx_path)
    wb.close()
    print(f"[beauty_worker] Saved: {xlsx_path}")


# ── Main watcher loop ─────────────────────────────────────────────────────────

def _find_ready_signals(watch_dir: str) -> list[str]:
    """Return sorted list of absolute paths to *.ready files."""
    try:
        return sorted(
            os.path.join(watch_dir, f)
            for f in os.listdir(watch_dir)
            if f.endswith(".ready")
        )
    except Exception:
        return []


def _batch_start_from_signal(signal_path: str) -> int:
    """
    Parse the batchStart integer from a signal filename.

    Expected filename pattern (set by Java's writeXlsxText):
        <batchStart> - <batchEnd>_refactoring_data.ready

    Returns the batchStart as an int, or -1 if parsing fails
    (which means the file will always be processed).
    """
    filename = os.path.basename(signal_path)          # e.g. "500 - 504_refactoring_data.ready"
    try:
        # Take everything before the first space-dash-space separator
        start_str = filename.split(" - ")[0].strip()
        return int(start_str)
    except (ValueError, IndexError):
        return -1


def main():
    parser = argparse.ArgumentParser(
        description="Beauty-metrics worker for the two-clone pipeline."
    )
    parser.add_argument("--watch-dir", required=True,
                        help="Directory containing xlsx files and .ready signals.")
    parser.add_argument("--clone-b",   required=True,
                        help="Path to the second git clone (used for checkouts).")
    parser.add_argument("--workers",   type=int, default=8,
                        help="Thread-pool size for per-file computation.")
    parser.add_argument("--poll",      type=float, default=2.0,
                        help="Polling interval in seconds.")
    parser.add_argument("--start-from-commit", type=int, default=0,
                        metavar="N",
                        help=("Skip every xlsx batch whose batchStart < N. "
                              "Use the same value as Java's startingCommitNumber "
                              "when resuming an interrupted run. (default: 0)"))
    parser.add_argument("--git-url", default=None,
                        metavar="URL",
                        help=("Git URL to clone into clone-b if it does not yet "
                              "exist.  When provided the worker auto-clones on "
                              "first startup so no manual git clone is needed."))
    parser.add_argument("--idle-confirms", type=int, default=5,
                        metavar="N",
                        help=("Number of consecutive idle poll ticks (sentinel present "
                              "AND no .ready files) required before exiting. "
                              "Prevents premature exit when Python catches up to Java. "
                              "(default: 5)"))
    args = parser.parse_args()

    watch_dir       = os.path.abspath(args.watch_dir)
    clone_b         = os.path.abspath(args.clone_b)
    max_workers     = args.workers
    poll_interval   = args.poll
    start_from      = args.start_from_commit   # batches with batchStart < this are skipped
    idle_confirms   = args.idle_confirms        # consecutive idle ticks before exit

    print(f"[beauty_worker] Started.")
    print(f"  watch-dir        : {watch_dir}")
    print(f"  clone-b          : {clone_b}")
    print(f"  git-url          : {args.git_url or '(not set – clone-b must already exist)'}")
    print(f"  workers          : {max_workers}")
    print(f"  poll             : {poll_interval}s")
    print(f"  start-from-commit: {start_from} "
          f"({'process all batches' if start_from == 0 else f'skip batches with batchStart < {start_from}'})")
    print(f"  idle-confirms    : {idle_confirms} consecutive idle ticks before exit")

    # ── Auto-clone Clone B if a git-url was given ────────────────────────────
    if args.git_url:
        _ensure_clone(args.git_url, clone_b)
    elif not os.path.isdir(clone_b):
        sys.exit(f"[beauty_worker] ERROR: clone-b does not exist and no --git-url provided: {clone_b}")

    if not os.path.isdir(watch_dir):
        os.makedirs(watch_dir, exist_ok=True)
        print(f"[beauty_worker] Created watch-dir: {watch_dir}")

    processed = set()   # guard against double-processing
    idle_count = 0      # consecutive ticks where sentinel is present but no .ready files

    while True:
        signals = _find_ready_signals(watch_dir)
        did_work = False

        for signal_path in signals:
            if signal_path in processed:
                continue

            # ── Resume support: skip batches that were already processed ──────
            batch_start = _batch_start_from_signal(signal_path)
            if batch_start != -1 and batch_start < start_from:
                print(f"[beauty_worker] SKIP (already collected): "
                      f"{os.path.basename(signal_path)} "
                      f"(batchStart={batch_start} < {start_from})")
                processed.add(signal_path)
                # Rename to .skip so the directory stays tidy
                skip_path = signal_path[:-len(".ready")] + ".skip"
                try:
                    os.rename(signal_path, skip_path)
                except Exception:
                    pass
                continue

            # Derive the xlsx path: replace .ready extension with .xlsx
            xlsx_path = signal_path[:-len(".ready")] + ".xlsx"

            if not os.path.isfile(xlsx_path):
                print(f"[beauty_worker] WARN: signal found but xlsx missing: {xlsx_path}",
                      file=sys.stderr)
                processed.add(signal_path)
                continue

            processed.add(signal_path)

            try:
                _patch_xlsx(xlsx_path, clone_b, max_workers)
                # Rename .ready → .done to signal completion
                done_path = signal_path[:-len(".ready")] + ".done"
                os.rename(signal_path, done_path)
                print(f"[beauty_worker] Done: {done_path}")
                did_work = True
            except Exception as exc:
                import traceback
                print(f"[beauty_worker] ERROR processing {xlsx_path}:",
                      file=sys.stderr)
                traceback.print_exc()
                # Rename to .error so it's not retried endlessly
                error_path = signal_path[:-len(".ready")] + ".error"
                try:
                    os.rename(signal_path, error_path)
                except Exception:
                    pass

        # ── Check for a global "all-done" sentinel written by Java ───────────
        # We only exit once the sentinel has been seen AND there are no
        # remaining .ready files for `idle_confirms` consecutive ticks.
        # This prevents premature shutdown when Python catches up to Java
        # mid-run (Java may still be about to write the next batch's .ready).
        all_done_sentinel = os.path.join(watch_dir, "ALL_DONE.sentinel")
        if os.path.isfile(all_done_sentinel):
            remaining = _find_ready_signals(watch_dir)
            if not remaining:
                idle_count += 1
                print(f"[beauty_worker] Sentinel present, no .ready files "
                      f"(idle tick {idle_count}/{idle_confirms}) — "
                      f"{'waiting for Java\u2026' if idle_count < idle_confirms else 'exiting.'}")
                if idle_count >= idle_confirms:
                    print("[beauty_worker] ALL_DONE confirmed after "
                          f"{idle_confirms} idle ticks. Exiting.")
                    break
            else:
                # New .ready files appeared — reset idle counter and let the
                # next iteration pick them up.
                if idle_count > 0:
                    print(f"[beauty_worker] Resetting idle counter "
                          f"({len(remaining)} new .ready file(s) appeared).")
                idle_count = 0
        else:
            # Sentinel not yet written — Java is still running.
            if did_work:
                idle_count = 0   # stay fresh while actively processing

        time.sleep(poll_interval)


if __name__ == "__main__":
    main()
