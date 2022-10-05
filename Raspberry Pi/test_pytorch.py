import os
import socket
import time
import os.path
from os import path
import _thread
import collage
import torch
from PIL import Image

host = '192.168.42.12'
port = 54321
port_image = 55555
folder_name = "C:\\Users\\Dayou\\Documents\\MDP\\COMBINEDCODE"
algo_folder_name = "C:\\Users\\Dayou\\IdeaProjects\\MDPALGO"

def TestFunc():
    print ("Testing function")
    os.chdir(folder_name + "\yolov5")
    # os.system(
    #     'python detect.py --weights best.pt --source images --img 416 --conf 0.3 --save-conf --hide-conf --name results --exist-ok --save-txt')
    os.system('python detect.py --weights best.pt --img 416 --conf 0.3 --save-conf --hide-conf --name results --exist-ok --save-txt --device 0 --source images/run1/')

    # sending image recognition results to RPI
    labelText = r"C:\Users\acer\COMBINEDCODE\yolov5\runs\detect\results\labels\images.txt"
    labelText = folder_name + "\\yolov5\\runs\\detect\\results\\labels\\images.txt"
    print("LabelText: " + labelText)

    if path.exists(labelText) is True:
        with open(labelText, 'r') as f:
            data = f.read()
        result = str(data)
        label = int(result[:2].strip()) + 11
        message = str(label)
        print('Image number: ' + message)

if __name__ == '__main__':
    TestFunc()