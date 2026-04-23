import math 
from utils import multiply_tables, create_distance_tables, calculate_middle_point, calculate_quadrant_sums

def calc_bmVert(x):
    wL = x["UL"] + x["LL"]
    wR = x["UR"] + x["LR"]
    maxW = max(wL, wR)
    if(maxW == 0):
        return 0
    
    return abs((wL-wR)/(maxW))

def calc_bmHor(y):
    wT = y["UL"] + y["UR"]
    wB = y["LL"] + y["LR"]
    maxW = max(wT, wB)
    if(maxW == 0):
        return 0
    
    return abs((wT-wB)/(maxW))

def calculate_balance(binary_table):
    middle_x, middle_y = calculate_middle_point(binary_table)

    temp_dist_x, temp_dist_y= create_distance_tables(binary_table, middle_x, middle_y)
    dist_x = multiply_tables(binary_table, temp_dist_x)
    dist_y = multiply_tables(binary_table, temp_dist_y)

    x_distance_quadrants = calculate_quadrant_sums(dist_x, middle_x, middle_y)
    y_distance_quadrants = calculate_quadrant_sums(dist_y, middle_x, middle_y)

    bmVert = calc_bmVert(x_distance_quadrants)
    # print(f"bmVert: {bmVert}")
    bmHor = calc_bmHor(y_distance_quadrants)
    # print(f"bmHor: {bmHor}")
    

    return 1-(bmVert+bmHor)/2