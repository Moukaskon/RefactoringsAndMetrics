import math 
from utils import sum_tb, frame_size, multiply_tables, create_distance_tables, calculate_middle_point, calculate_start_end_positions, lines_quadrants, chars_quadrants, normalize_quadrant_values, divide_tables, calculate_distances_middle

#OK ckd

def emX(dist_x, b_frame, total_chars):
    if(total_chars==0 or b_frame==0):
        return 0
    
    emx = 2*(sum_tb(dist_x))/(b_frame*total_chars)
    return emx

def emY(dist_y, h_frame, total_chars):
    if(total_chars==0 or h_frame==0):
        return 0
    
    emy = 2*(sum_tb(dist_y))/(h_frame*total_chars)
    return emy

def calculate_equilibrium(binary_table):
    h_frame, b_frame = frame_size(binary_table)
    
    # print(f"h_frame: {h_frame}")
    # print(f"b_frame: {b_frame}")
    middle_x, middle_y = calculate_middle_point(binary_table)

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
    total_chars = sum_tb(binary_table)
    # print(f"equilib: total chars: {total_chars}")
    
    emx = emX(dist_x, b_frame, total_chars)
    # print(f"emX: {emx}")
    emy = emY(dist_y, h_frame, total_chars)
    # print(f"emY: {emy}")

    em = 1- (abs(emx)+abs(emy))/2
    return em