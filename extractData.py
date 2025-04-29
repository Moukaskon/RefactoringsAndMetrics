import os
import pandas as pd

# Define the refactorings
REFACTORINGS = [
    "EXTRACT_METHOD", "MOVE_METHOD", "PULL_UP_METHOD", "EXTRACT_SUPERCLASS",
    "EXTRACT_INTERFACE", "EXTRACT_AND_MOVE_METHOD", "EXTRACT_CLASS",
    "MOVE_AND_RENAME_METHOD", "SPLIT_CLASS"
]

# Directory containing the CSV files
PROJECT_DIR = r"C:\Users\Kalo\Desktop\NewRefs\RefactoringsAndMetrics"

# Output file (Excel format for consistency)
OUTPUT_FILE = os.path.join(PROJECT_DIR, "aggregated_refactoringsTest.xlsx")

def count_refactorings_in_file(file_path):
    try:
        df = pd.read_csv(file_path)
        counts = {refactoring: 0 for refactoring in REFACTORINGS}
        for refactoring in REFACTORINGS:
            counts[refactoring] = df[df.isin([refactoring]).any(axis=1)].shape[0]
        return counts
    except Exception as e:
        print(f"Error processing file {file_path}: {e}")
        return {refactoring: 0 for refactoring in REFACTORINGS}

def aggregate_refactorings():
    data = []
    for root, _, files in os.walk(PROJECT_DIR):
        if root == PROJECT_DIR:
            for file in files:
                if file.endswith(".csv"):
                    file_path = os.path.join(root, file)
                    print(f"Processing {file_path}")
                    project_name = os.path.splitext(file)[0]
                    refactoring_counts = count_refactorings_in_file(file_path)
                    row = {"Project": project_name, **refactoring_counts}
                    data.append(row)

    df = pd.DataFrame(data)

    sum_row = {"Project": "TOTAL"}
    for col in df.columns[1:]:
        sum_row[col] = df[col].sum()

    df = pd.concat([df, pd.DataFrame([sum_row])], ignore_index=True)

    df.to_excel(OUTPUT_FILE, index=False)
    print(f"Aggregated data saved to {OUTPUT_FILE}")

if __name__ == "__main__":
    aggregate_refactorings()
