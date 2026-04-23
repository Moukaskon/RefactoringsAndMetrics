import os
import csv
import sys
from simplicity import calculate_simplicity
from symmetry import calculate_symmetry
from equilibrium import calculate_equilibrium
from rhythm import calculate_rhythm
from regularity import calculate_regularity
from sequence import calculate_sequence
from density import calculate_density
from balance import calculate_balance


def process_file(file_path):
    try:
        with open(file_path, 'r', encoding='utf-8') as file:
            lines = file.readlines()
    except UnicodeDecodeError:
        with open(file_path, 'r', encoding='latin1') as file:
            lines = file.readlines()

    # Remove empty lines at the start and end of the file
    while lines and not lines[0].strip():
        lines.pop(0)
    while lines and not lines[-1].strip():
        lines.pop()

    if not lines:
        return [file_path, 0, 0, 0, 0, 0, 0, 0, 0]

    # Calculate table dimensions
    num_lines = len(lines)
    max_line_length = max(
        sum(4 if char == '\t' else 1 for char in line.rstrip('\n')) for line in lines
    )

    # Initialize a table with dimensions based on max line and file lines
    table_rows = num_lines
    table_cols = max_line_length
    table = [[' ' for _ in range(table_cols)] for _ in range(table_rows)]

    # Populate the table line by line
    for row_idx, line in enumerate(lines):
        col_idx = 0
        for char in line.rstrip('\n'):
            if char == '\t':
                col_idx += 4  # Tab character adds 4 spaces
            else:
                if row_idx < table_rows and col_idx < table_cols:
                    table[row_idx][col_idx] = char
                col_idx += 1

    # Convert the table into a binary representation
    binary_table = [[1 if cell.strip() else 0 for cell in row] for row in table]

    simplicity   = calculate_simplicity(binary_table)
    regularity   = calculate_regularity(binary_table)
    symmetry     = calculate_symmetry(binary_table)
    equilibrium  = calculate_equilibrium(binary_table)
    rhythm       = calculate_rhythm(binary_table)
    sequence     = calculate_sequence(binary_table)
    density      = calculate_density(binary_table)
    balance      = calculate_balance(binary_table)

    return [
        file_path,
        balance,
        equilibrium,
        density,
        regularity,
        rhythm,
        sequence,
        simplicity,
        symmetry
    ]


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: aesthetics_main.py <project_directory>")
        sys.exit(1)

    project_dir = sys.argv[1]
    project_name = os.path.basename(os.path.normpath(project_dir))
    output_csv = os.path.join(project_dir, project_name + "_beauty.csv")

    java_files = []
    for root, dirs, files in os.walk(project_dir):
        # Skip hidden directories (e.g. .git)
        dirs[:] = [d for d in dirs if not d.startswith('.')]
        for f in files:
            if f.endswith('.java'):
                java_files.append(os.path.join(root, f))

    print(f"Processing {len(java_files)} Java files in {project_dir} ...")

    with open(output_csv, 'w', newline='', encoding='utf-8') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(['file', 'balance', 'equilibrium', 'density',
                         'regularity', 'rhythm', 'sequence', 'simplicity', 'symmetry'])

        for java_file in java_files:
            try:
                metrics = process_file(java_file)
                # Make the key relative to the project directory,
                # normalised to forward slashes and lowercase
                # to match Java's normalizePath() output.
                rel_path = os.path.relpath(java_file, project_dir)
                rel_path = rel_path.replace('\\', '/').lower()
                metrics[0] = rel_path
                writer.writerow(metrics)
            except Exception as e:
                print(f"Error processing {java_file}: {e}")

    print(f"Beauty metrics written to {output_csv}")
