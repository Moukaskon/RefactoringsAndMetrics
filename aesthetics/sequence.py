import math 
from utils import multiply_tables, create_distance_tables, calculate_middle_point, calculate_quadrant_sums

#OK ckd

def calc_dif(v):
    #calc Σ|q-v|
    q = {
        "UL": 4,  # Upper Left
        "UR": 3,  # Upper Right
        "LL": 2,   # Lower Left
        "LR": 1   # Lower Right
    }
    
    return {key: abs(q[key] - v[key]) for key in q if key in v}

def calc_w(chars):
    q = {
        "UL": 4,  # Upper Left
        "UR": 3,  # Upper Right
        "LL": 2,   # Lower Left
        "LR": 1   # Lower Right
    }

    return {key: q[key] * chars[key] for key in q if key in chars}

def rank_dict_by_values(original_dict):
    """
    Ranks the keys of a dictionary based on their values in ascending order.

    Args:
    - original_dict (dict): The dictionary to rank.

    Returns:
    - dict: A new dictionary where each key has a value representing its rank.
    """
    # Sort the items by value and enumerate to assign ranks
    sorted_items = sorted(original_dict.items(), key=lambda item: item[1])
    ranked_dict = {key: rank + 1 for rank, (key, _) in enumerate(sorted_items)}
    return ranked_dict

def calculate_sequence(binary_table):
    middle_x, middle_y = calculate_middle_point(binary_table)

    # Calculate the number of chars in each quadrant
    # B (all quadrants)
    char_count_quadrants = calculate_quadrant_sums(binary_table, middle_x, middle_y)
    # print(f"SA Quadrant char counts: {char_count_quadrants}")

    w = calc_w(char_count_quadrants)
    # print("q: ")
    # print(w)

    v = rank_dict_by_values(w)
    # print("V: ")
    # print(v)

    dif = calc_dif(v)
    # print(dif)

    sqm = 1-sum(dif.values())/8

    return sqm
