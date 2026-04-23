import math 
from utils import multiply_tables, calculate_start_end_positions, create_distance_tables, calculate_middle_point, calculate_quadrant_sums, lines_quadrants, chars_quadrants, normalize_quadrant_values, divide_tables, calculate_distances_middle

def calculate_R(dist_y, dist_x):
    """
    calculates R
    """
    rows = len(dist_y)
    cols = len(dist_y[0]) if rows > 0 else 0

    # Initialize the result table
    r_tb = [[0 for _ in range(cols)] for _ in range(rows)]

    for row_idx in range(rows):
        for col_idx in range(cols):
            r_tb[row_idx][col_idx] = math.sqrt((dist_y[row_idx][col_idx])**2 + (dist_x[row_idx][col_idx])**2)

    return r_tb

def calc_vertical(h, b, x, y, th, r):
    sym_vert = 0
    sym_vert += abs(x["UL"]-x["UR"]) + abs(x["LL"]-x["LR"])
    sym_vert += abs(y["UL"]-y["UR"]) + abs(y["LL"]-y["LR"])
    sym_vert += abs(h["UL"]-h["UR"]) + abs(h["LL"]-h["LR"])
    sym_vert += abs(b["UL"]-b["UR"]) + abs(b["LL"]-b["LR"])
    sym_vert += abs(th["UL"]-th["UR"]) + abs(th["LL"]-th["LR"])
    sym_vert += abs(r["UL"]-r["UR"]) + abs(r["LL"]-r["LR"])

    sym_vert = sym_vert/12

    return sym_vert

def calc_horizontal(h, b, x, y, th, r):
    sym_hor = 0
    sym_hor += abs(x["UL"]-x["LL"]) + abs(x["UR"]-x["LR"])
    sym_hor += abs(y["UL"]-y["LL"]) + abs(y["UR"]-y["LR"])
    sym_hor += abs(h["UL"]-h["LL"]) + abs(h["UR"]-h["LR"])
    sym_hor += abs(b["UL"]-b["LL"]) + abs(b["UR"]-b["LR"])
    sym_hor += abs(th["UL"]-th["LL"]) + abs(th["UR"]-th["LR"])
    sym_hor += abs(r["UL"]-r["LL"]) + abs(r["UR"]-r["LR"])

    sym_hor = sym_hor/12
    
    return sym_hor

def calc_radial(h, b, x, y, th, r):
    sym_rad = 0
    sym_rad += abs(x["UL"]-x["LR"]) + abs(x["UR"]-x["LL"])
    sym_rad += abs(y["UL"]-y["LR"]) + abs(y["UR"]-y["LL"])
    sym_rad += abs(h["UL"]-h["LR"]) + abs(h["UR"]-h["LL"])
    sym_rad += abs(b["UL"]-b["LR"]) + abs(b["UR"]-b["LL"])
    sym_rad += abs(th["UL"]-th["LR"]) + abs(th["UR"]-th["LL"])
    sym_rad += abs(r["UL"]-r["LR"]) + abs(r["UR"]-r["LL"])

    sym_rad = sym_rad/12
    
    return sym_rad



def calculate_symmetry(binary_table):
    middle_x, middle_y = calculate_middle_point(binary_table)

    # Calculate the start and end positions for each line
    positions_table = calculate_start_end_positions(binary_table)

    # Calculate the number of chars in each quadrant
    # B (all quadrants)
    char_count_quadrants = calculate_quadrant_sums(binary_table, middle_x, middle_y)
    # print(f"Quadrant char counts: {char_count_quadrants}")
    norm_char_count = normalize_quadrant_values(char_count_quadrants)
    # print(f"norm_char_count: {norm_char_count}")


    # Calculate the number of lines in each quadrant
    # H (all quadrants)
    line_count_quadrants = lines_quadrants(positions_table, middle_x, middle_y)
    norm_line_count = normalize_quadrant_values(line_count_quadrants)
    # print(f"Quadrant line counts: {line_count_quadrants}")
    # print(f"norm_line_count: {norm_line_count}")
    # norm_line_count = norm_char_count # since a char is 1x1

    # Calculate the distances
    # dist_x, dist_y = calculate_distances_middle(binary_table, middle_x, middle_y)
    temp_dist_x, temp_dist_y= create_distance_tables(binary_table, middle_x, middle_y)
    dist_x = multiply_tables(binary_table, temp_dist_x)
    dist_y = multiply_tables(binary_table, temp_dist_y)
    # # Print the results
    # print("dist_x:")
    # for row in dist_x:
    #     print(row)
    # print("\ndist_y:")
    # for row in dist_y:
    #     print(row)

    x_distance_quadrants = calculate_quadrant_sums(dist_x, middle_x, middle_y)
    # print(x_distance_quadrants)
    norm_x_count = normalize_quadrant_values(x_distance_quadrants)

    y_distance_quadrants = calculate_quadrant_sums(dist_y, middle_x, middle_y)
    # print(y_distance_quadrants)
    norm_y_count = normalize_quadrant_values(y_distance_quadrants)

    th_table = divide_tables(dist_y, dist_x)
    # print("\nth_table:")
    # for row in th_table:
    #     print(row)
    th_quadrants = calculate_quadrant_sums(th_table, middle_x, middle_y)
    # print(th_quadrants)
    norm_th = normalize_quadrant_values(th_quadrants)

    r_table = calculate_R(dist_y, dist_x)
    r_quadrants = calculate_quadrant_sums(r_table, middle_x, middle_y)
    # print(r_quadrants)
    norm_r = normalize_quadrant_values(r_quadrants)

    sym_rad = calc_radial(norm_line_count, norm_char_count, norm_x_count, norm_y_count, norm_th, norm_r)
    sym_hor = calc_horizontal(norm_line_count, norm_char_count, norm_x_count, norm_y_count, norm_th, norm_r)
    sym_vert = calc_vertical(norm_line_count, norm_char_count, norm_x_count, norm_y_count, norm_th, norm_r)
    sym = 1 - (sym_rad+sym_hor+sym_vert)/3

    return sym