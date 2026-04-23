import math 
from utils import sum_tb, frame_size

def calculate_density(binary_table):   

    num_of_chars = sum_tb(binary_table)
    yframe, xframe = frame_size(binary_table)
    aframe = yframe*xframe
    if(aframe==0):
        return 0

    # print(f"aframe : {aframe}")
    # print(f"num_of_chars : {num_of_chars}")

    return 1-2*abs(0.5-num_of_chars/aframe)