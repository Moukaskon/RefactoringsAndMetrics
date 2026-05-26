"""
aesthetics_server.py  –  long-lived Python process for beauty metrics.

Protocol (newline-delimited JSON over stdin/stdout):
  Java writes:  {"projectPath": "/abs/path/to/project"}
  Python writes: {"status": "ok", "metrics": {"rel/file.java": [b,e,d,r,rh,sq,si,sy], ...}}
               or {"status": "error", "message": "..."}

Java starts this process once at the beginning, keeps it alive, and
sends one request per commit checkout.  No interpreter restart overhead.
"""

import sys
import os
import json
import concurrent.futures

# Ensure the directory containing this script is on sys.path so sibling
# modules (simplicity, symmetry, …) can be imported regardless of cwd.
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


# ---------------------------------------------------------------------------
# Per-file computation (same logic as aesthetics_main.py → process_file)
# ---------------------------------------------------------------------------

def _process_file(file_path):
    """Return [balance, equilibrium, density, regularity, rhythm, sequence, simplicity, symmetry] or raise."""
    try:
        with open(file_path, 'r', encoding='utf-8') as fh:
            lines = fh.readlines()
    except UnicodeDecodeError:
        with open(file_path, 'r', encoding='latin1') as fh:
            lines = fh.readlines()

    # Strip leading / trailing blank lines
    while lines and not lines[0].strip():
        lines.pop(0)
    while lines and not lines[-1].strip():
        lines.pop()

    if not lines:
        return [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]

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

    binary_table = [[1 if cell.strip() else 0 for cell in row] for row in table]

    return [
        calculate_balance(binary_table),
        calculate_equilibrium(binary_table),
        calculate_density(binary_table),
        calculate_regularity(binary_table),
        calculate_rhythm(binary_table),
        calculate_sequence(binary_table),
        calculate_simplicity(binary_table),
        calculate_symmetry(binary_table),
    ]


def _process_project(project_dir):
    """Walk project_dir, compute metrics for every .java file in parallel."""
    java_files = []
    for root, dirs, files in os.walk(project_dir):
        dirs[:] = [d for d in dirs if not d.startswith('.')]
        for f in files:
            if f.endswith('.java'):
                java_files.append(os.path.join(root, f))

    results = {}

    # ThreadPoolExecutor: file I/O is the bottleneck, threads are safe here.
    max_workers = min(32, os.cpu_count() or 4)
    with concurrent.futures.ThreadPoolExecutor(max_workers=max_workers) as pool:
        future_to_path = {
            pool.submit(_process_file, fp): fp
            for fp in java_files
        }
        for future in concurrent.futures.as_completed(future_to_path):
            fp = future_to_path[future]
            rel = os.path.relpath(fp, project_dir).replace('\\', '/').lower()
            try:
                results[rel] = future.result()
            except Exception as exc:
                sys.stderr.write(f"[aesthetics_server] error on {fp}: {exc}\n")
                results[rel] = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]

    return results


# ---------------------------------------------------------------------------
# Main server loop – read requests from stdin, write responses to stdout
# ---------------------------------------------------------------------------

def main():
    sys.stderr.write("[aesthetics_server] ready\n")
    sys.stderr.flush()

    for raw_line in sys.stdin:
        raw_line = raw_line.strip()
        if not raw_line:
            continue

        try:
            request = json.loads(raw_line)
            project_path = request["projectPath"]

            metrics = _process_project(project_path)

            response = {"status": "ok", "metrics": metrics}
        except Exception as exc:
            response = {"status": "error", "message": str(exc)}

        # Write exactly one JSON line back so Java can readline()
        sys.stdout.write(json.dumps(response) + '\n')
        sys.stdout.flush()


if __name__ == "__main__":
    main()
