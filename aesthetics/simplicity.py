from utils import calculate_nvap_and_nhap, group_lines_by_indentation, group_lines_by_indentation_2


def calculate_simplicity(binary_table):
    # Calculate the distinct indentation levels and empty lines from the binary_table
    nvap, nhap = calculate_nvap_and_nhap(binary_table)

    # print(f"nvap: {nvap}")
    # print(f"nhap: {nhap}")
    # Group lines based on indentation
    groups = group_lines_by_indentation(binary_table)
    
    # print(f"n: {len(groups)}")
    # groups=group_lines_by_indentation_2(binary_table)

    return 3/(len(groups)+nvap+nhap)

