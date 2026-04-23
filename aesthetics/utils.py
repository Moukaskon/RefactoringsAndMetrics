def group_leading_space_differences(binary_table):
    """
    Analyze the differences in the number of leading spaces between consecutive lines.

    Args:
    - binary_table (list of list of int): The binary table to analyze.

    Returns:
    - difference_counts (dict): A dictionary where keys are the differences in leading spaces,
                                and values are the counts of those differences.
    - distinct_differences (int): The number of distinct leading space differences.
    """
    difference_counts = {}
    previous_leading_spaces = None

    for row in binary_table:
        # Check if the row is empty (all zeros)
        if all(cell == 0 for cell in row):
            continue  # Skip completely empty lines

        # Count leading spaces in the current row
        leading_spaces = 0
        for cell in row:
            if cell == 0:
                leading_spaces += 1
            else:
                break

        if previous_leading_spaces is not None:
            # Calculate the absolute difference in leading spaces
            difference = abs(leading_spaces - previous_leading_spaces)
            if difference > 0:  # Ignore zero differences
                difference_counts[difference] = difference_counts.get(difference, 0) + 1

        # Update the previous leading spaces
        previous_leading_spaces = leading_spaces

    # Calculate the number of distinct differences
    distinct_differences = len(difference_counts)

    return difference_counts, distinct_differences



def groups_blank_lines(binary_table):
    """
    Analyzes sequences of blank lines in a binary table.

    Args:
    - binary_table (list of list of int): The binary table.

    Returns:
    - group_counts (dict): A dictionary where keys are the lengths of blank-line sequences
                           and values are their counts.
    - distinct_groups (int): The number of distinct groups (unique lengths of blank-line sequences).
    """
    group_counts = {}
    consecutive_blank_lines = 0

    for row in binary_table:
        if all(cell == 0 for cell in row):  # Check if the line is blank
            consecutive_blank_lines += 1
        else:
            if consecutive_blank_lines > 0:
                # Increment the count for the length of the blank-line sequence
                group_counts[consecutive_blank_lines] = group_counts.get(consecutive_blank_lines, 0) + 1
                consecutive_blank_lines = 0

    # Handle the case where the last lines in the table are blank
    if consecutive_blank_lines > 0:
        group_counts[consecutive_blank_lines] = group_counts.get(consecutive_blank_lines, 0) + 1

    distinct_groups = len(group_counts)
    return group_counts, distinct_groups


def calculate_nvap_and_nhap(binary_table):
    """
    This function calculates:
    - nvap: the number of distinct indentation levels.
    - nhap: the number of empty lines.
    
    Args:
    - binary_table (list): A 2D list representing the binary table.
    
    Returns:
    - (nvap, nhap): Tuple containing the number of distinct indentations and empty lines.
    """
    distinct_indentations = set()
    empty_line_count = 0

    for row in binary_table:
        # Count empty lines (rows with all 0's)
        if all(cell == 0 for cell in row):
            empty_line_count += 1
            continue
        
        # Identify indentation level (position of the first 1 in the row)
        indentation_level = next((idx for idx, cell in enumerate(row) if cell == 1), None)
        if indentation_level is not None:
            distinct_indentations.add(indentation_level)

    # The number of distinct indentation levels (nvap)
    nvap = len(distinct_indentations)
    # The number of empty lines (nhap)
    nhap = empty_line_count
    
    return nhap, nvap

def group_lines_by_indentation_2(binary_table):
    groups = []
    current_group = []
    previous_indentation = None

    for row_idx, row in enumerate(binary_table):
        # Check if the line is empty or has only one `1`
        if all(cell == 0 for cell in row) or row.count(1) == 1:  
            if current_group:
                groups.append(current_group)
                current_group = []
            previous_indentation = None
        else:
            current_indentation = next((idx for idx, cell in enumerate(row) if cell == 1), None)
            if previous_indentation is None or current_indentation != previous_indentation:
                if current_group:
                    groups.append(current_group)
                current_group = [row_idx]
            else:
                current_group.append(row_idx)
            previous_indentation = current_indentation

    if current_group:  # Add the last group if it exists
        groups.append(current_group)

    return groups

def group_lines_by_indentation(binary_table):
    groups = []
    current_group = []
    previous_indentation = None

    for row_idx, row in enumerate(binary_table):
        if all(cell == 0 for cell in row):  # Empty line starts a new group
            if current_group:
                groups.append(current_group)
                current_group = []
            previous_indentation = None
        else:
            current_indentation = next((idx for idx, cell in enumerate(row) if cell == 1), None)
            if previous_indentation is None or current_indentation != previous_indentation:
                if current_group:
                    groups.append(current_group)
                current_group = [row_idx]
            else:
                current_group.append(row_idx)
            previous_indentation = current_indentation

    if current_group:  # Add the last group if it exists
        groups.append(current_group)

    return groups


def sum_tb(table):
    """
    Counts the number of cells.

    Args:
    - binary_table (list of list of int): The binary table.

    Returns:
    - count (int): The total number of cells containing 1.
    """
    return sum(cell for row in table for cell in row)


def frame_size(binary_table):
    rows = len(binary_table)
    cols = len(binary_table[0]) if rows > 0 else 0

    return rows, cols

def calculate_middle_point(binary_table):
    """
    This function calculates the middle point of the binary table's x and y axes.
    
    Args:
    - binary_table (list): A 2D list representing the binary table.
    
    Returns:
    - (middle_x, middle_y): Tuple containing the x and y coordinates of the middle point.
    """
    # Get the number of rows (height) and columns (width)
    num_rows = len(binary_table)
    num_cols = len(binary_table[0]) if num_rows > 0 else 0

    # Calculate the middle point coordinates
    middle_x = (num_cols - 1) // 2
    middle_y = (num_rows - 1) // 2

    return middle_x, middle_y

def create_distance_tables(binary_table, middle_x, middle_y):
    """
    Creates two tables where each cell contains its distance from the middle x and middle y coordinates.

    Args:
    - binary_table (list of list of int): The binary table representing the code (1 for character, 0 for empty).
    - middle_x (int): The middle x-coordinate of the table.
    - middle_y (int): The middle y-coordinate of the table.

    Returns:
    - dist_x (list of list of int): A table of distances from middle_x.
    - dist_y (list of list of int): A table of distances from middle_y.
    """
    rows = len(binary_table)
    cols = len(binary_table[0]) if rows > 0 else 0

    # Initialize distance tables with the same dimensions as the binary table
    dist_x = [[0 for _ in range(cols)] for _ in range(rows)]
    dist_y = [[0 for _ in range(cols)] for _ in range(rows)]

    # Fill the distance tables
    for row_idx in range(rows):
        for col_idx in range(cols):
            dist_x[row_idx][col_idx] = abs(col_idx - middle_x)
            dist_y[row_idx][col_idx] = abs(row_idx - middle_y)

    return dist_x, dist_y

def calculate_quadrant_sums(binary_table, middle_x, middle_y):
    """
    Calculates the sum of 1s in each of the four quadrants of the binary table.
    
    Args:
    - binary_table (list): A 2D list representing the binary table.
    - middle_x (int): The middle index for the x-axis (columns).
    - middle_y (int): The middle index for the y-axis (rows).
    
    Returns:
    - A tuple with four integers representing the sum of 1s in:
      (top-left, top-right, bottom-left, bottom-right) quadrants.
    """
    # Initialize sums for each quadrant
    ul = 0
    ur = 0
    ll = 0
    lr = 0

    # Calculate sums for each quadrant
    for y, row in enumerate(binary_table):
        for x, value in enumerate(row):
            if y <= middle_y and x <= middle_x:  # Top-left quadrant
                ul += value
            elif y <= middle_y and x > middle_x:  # Top-right quadrant
                ur += value
            elif y > middle_y and x <= middle_x:  # Bottom-left quadrant
                ll += value
            elif y > middle_y and x > middle_x:  # Bottom-right quadrant
                lr += value

        # Initialize counters for each quadrant
    quadrant_counts = {
        "UR": ur,  # Upper Right
        "UL": ul,  # Upper Left
        "LR": lr,  # Lower Right
        "LL": ll   # Lower Left
    }

    return quadrant_counts

def calculate_start_end_positions(binary_table):
    """
    This function calculates the starting and ending positions (column indices) for each line
    in the binary table based on the first and last non-zero cell.
    
    Args:
    - binary_table (list): A 2D list representing the binary table.
    
    Returns:
    - positions_table (list): A 2D list where each row contains the start and end positions of the corresponding line.
    """
    positions_table = []

    for row in binary_table:
        # # Skip empty lines (lines that contain only 0's)
        # if all(cell == 0 for cell in row):
        #     continue

        # Find the first and last non-zero cells in the row
        start_position = next((idx for idx, cell in enumerate(row) if cell != 0), None)
        end_position = next((idx for idx in range(len(row)-1, -1, -1) if row[idx] != 0), None)

        # Append the start and end positions to the positions table
        positions_table.append((start_position, end_position))

    return positions_table

def lines_quadrants(positions_table, middle_x, middle_y):
    """
    This function calculates the number of lines in each of the four quadrants:
    Upper Right (UR), Upper Left (UL), Lower Right (LR), Lower Left (LL).

    Returns:
    - quadrant_counts (dict): A dictionary containing the count of lines in each quadrant.
    """
    # Initialize counters for each quadrant
    quadrant_counts = {
        "UR": 0,  # Upper Right
        "UL": 0,  # Upper Left
        "LR": 0,  # Lower Right
        "LL": 0   # Lower Left
    }

    for idx, (start, end) in enumerate(positions_table):
        if(start==None):
            continue
        if idx <= middle_y:  # Upper half
            if start <= middle_x:  # Start in the UR
                quadrant_counts["UL"] += 1
            if end > middle_x:  # End in the UL
                quadrant_counts["UR"] += 1
        else:  # Lower half
            if start <= middle_x:  # Start in the LR
                quadrant_counts["LL"] += 1
            if end > middle_x:  # End in the LL
                quadrant_counts["LR"] += 1


    return quadrant_counts

def chars_quadrants(positions_table, middle_x, middle_y):
    """
    This function calculates the number of chars in each of the four quadrants:
    Upper Right (UR), Upper Left (UL), Lower Right (LR), Lower Left (LL).

    Returns:
    - quadrant_counts (dict): A dictionary containing the count of lines in each quadrant.
    """
    # Initialize counters for each quadrant
    quadrant_counts = {
        "UR": 0,  # Upper Right
        "UL": 0,  # Upper Left
        "LR": 0,  # Lower Right
        "LL": 0   # Lower Left
    }

    for idx, (start, end) in enumerate(positions_table):
        if(start==None):
            continue
        if idx <= middle_y:  # Upper half
            if start <= middle_x:  # Start in the UR
                quadrant_counts["UL"] += middle_x-start
            if end > middle_x:  # End in the UL
                quadrant_counts["UR"] += end-middle_x
        else:  # Lower half
            if start <= middle_x:  # Start in the LR
                quadrant_counts["LL"] += middle_x-start
            if end > middle_x:  # End in the LL
                quadrant_counts["LR"] += end-middle_x


    return quadrant_counts

def calculate_distances_middle(binary_table, middle_x, middle_y):
    """
    Calculate the distances of each character from middle_x and middle_y.

    Args:
    - binary_table (list of list of int): The binary table representing the code (1 for character, 0 for empty).
    - middle_x (int): The middle x-coordinate of the table.
    - middle_y (int): The middle y-coordinate of the table.

    Returns:
    - dist_x (list of list of int): The table of x-distances from middle_x.
    - dist_y (list of list of int): The table of y-distances from middle_y.
    """
    rows = len(binary_table)
    cols = len(binary_table[0]) if rows > 0 else 0

    # Initialize distance tables with the same dimensions as binary_table
    dist_x = [[0 for _ in range(cols)] for _ in range(rows)]
    dist_y = [[0 for _ in range(cols)] for _ in range(rows)]

    for row_idx, row in enumerate(binary_table):
        for col_idx, cell in enumerate(row):
            if cell == 1:  # Only calculate distances for cells with a character
                dist_x[row_idx][col_idx] = abs(col_idx - middle_x)
                dist_y[row_idx][col_idx] = abs(row_idx - middle_y)

    return dist_x, dist_y

def divide_tables(dist_y, dist_x):
    """
    Divides the values of dist_y by dist_x element-wise, avoiding division by zero.

    Args:
    - dist_y (list of list of int): Table of y-distances from the middle.
    - dist_x (list of list of int): Table of x-distances from the middle.

    Returns:
    - dist_ratio (list of list of float): Table of the ratio dist_y / dist_x.
    """
    rows = len(dist_y)
    cols = len(dist_y[0]) if rows > 0 else 0

    # Initialize the result table
    dist_ratio = [[0 for _ in range(cols)] for _ in range(rows)]

    for row_idx in range(rows):
        for col_idx in range(cols):
            if dist_x[row_idx][col_idx] != 0:  # Avoid division by zero
                dist_ratio[row_idx][col_idx] = dist_y[row_idx][col_idx] / dist_x[row_idx][col_idx]
            else:
                dist_ratio[row_idx][col_idx] = 0  # Or use None or another placeholder if needed

    return dist_ratio

def multiply_tables(tb1, tb2):
    """
    multiplies the values of dist_y by dist_x element-wise, avoiding division by zero.

    """
    rows = len(tb1)
    cols = len(tb1[0]) if rows > 0 else 0

    # Initialize the result table
    dist_ratio = [[0 for _ in range(cols)] for _ in range(rows)]

    for row_idx in range(rows):
        for col_idx in range(cols):
            dist_ratio[row_idx][col_idx] = tb1[row_idx][col_idx] * tb2[row_idx][col_idx]

    return dist_ratio

def _sum_per_quadrant(positions_table, distance_tb, middle_x, middle_y):
    """

    """
    # Initialize counters for each quadrant
    quadrant_counts = {
        "UR": 0,  # Upper Right
        "UL": 0,  # Upper Left
        "LR": 0,  # Lower Right
        "LL": 0   # Lower Left
    }

    for idx, (start, end) in enumerate(positions_table):
        if start is None:  # Skip rows without any non-zero cells
            continue
        #TODO check the number of chars in each quadrant. 
        # Maybe there is something here, because a char cannot be on top of the middle point, so i need to look into that
        if idx <= middle_y:  # Upper half
            for i in range(start, middle_x+1):  # Characters in UL
                quadrant_counts["UL"] += distance_tb[idx][i]
            for i in range(middle_x+1, end):  # Characters in UR
                quadrant_counts["UR"] += distance_tb[idx][i]
        else:  # Lower half
            for i in range(start, middle_x+1):  # Characters in LL
                quadrant_counts["LL"] += distance_tb[idx][i]
            for i in range(middle_x+1, end):  # Characters in LR
                quadrant_counts["LR"] += distance_tb[idx][i]

    return quadrant_counts

def normalize_quadrant_values(quadrant_values):
    """
    Normalizes the values in a dictionary based on their sum.

    Args:
    - quadrant_values (dict): A dictionary containing quadrant values.

    Returns:
    - normalized_values (dict): A dictionary with normalized values.
    """
    total = sum(quadrant_values.values())
    
    if total == 0:  # To avoid division by zero
        return {key: 0 for key in quadrant_values}
    
    normalized_values = {key: value / total for key, value in quadrant_values.items()}
    return normalized_values