from utils import group_leading_space_differences, groups_blank_lines, calculate_nvap_and_nhap, group_lines_by_indentation

#OK ckd

def calc_rmAlign(nvap, nhap, n):
    if(n==1):
        return 1
    
    return abs(1- (nvap+nhap)/(2*n))

def calc_rmSpac(n, nSpacing):
    if(n==1):
        return 1
    
    return abs(1- (nSpacing-1)/(2*(n-1)))
    

def calculate_regularity(binary_table):
    # Calculate the distinct indentation levels and empty lines from the binary_table
    nvap, nhap = calculate_nvap_and_nhap(binary_table)

    # Group lines based on indentation
    groups = group_lines_by_indentation(binary_table)
    n = len(groups)

    rmAlign = calc_rmAlign(nvap, nhap, n)
    # print(f"rmAlign : {rmAlign}")

    _, n_h_spacing = groups_blank_lines(binary_table)
    _, n_v_spacing = group_leading_space_differences(binary_table)

    rmSpacing = calc_rmSpac(n, n_h_spacing+n_v_spacing)
    # print(f"rmSpacing : {rmSpacing}")
    
    
    rm = (rmAlign+rmSpacing)/2

    return rm

