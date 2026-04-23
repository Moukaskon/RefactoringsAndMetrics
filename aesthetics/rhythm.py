import math 
from utils import multiply_tables, create_distance_tables, calculate_middle_point, calculate_quadrant_sums, lines_quadrants, chars_quadrants, normalize_quadrant_values

#OK ckd

def rhm_part(x):
    rhm = 0
    rhm += abs(x["UL"]-x["UR"]) + abs(x["UL"]-x["LR"])
    rhm += abs(x["UL"]-x["LL"]) + abs(x["UR"]-x["LR"])
    rhm += abs(x["UR"]-x["LL"]) + abs(x["LR"]-x["LL"])

    rhm = rhm/6

    return abs(rhm)



def calculate_rhythm(binary_table):
    middle_x, middle_y = calculate_middle_point(binary_table)

    # Calculate the number of chars in each quadrant
    # B (all quadrants)
    char_count_quadrants = calculate_quadrant_sums(binary_table, middle_x, middle_y)
    # print(f"Quadrant char counts: {char_count_quadrants}")
    norm_char_count = normalize_quadrant_values(char_count_quadrants)

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

    rhmX = rhm_part(norm_x_count)
    # print(f"rhmX: {rhmX}")
    rhmY = rhm_part(norm_y_count)
    # print(f"rhmY: {rhmY}")
    rhmArea = rhm_part(norm_char_count)
    # print(f"rhmArea: {rhmArea}")

    rhm = 1- (rhmX+rhmY+rhmArea)/3

    return rhm