# on PC
# receives RPI images
# runs detect.py for image recognition on image received
# sends RPI info on the image detected

import os
import socket
import time
import os.path
from os import path

host = '10.27.150.156'
port = 54321

def setupServer():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    print("[S] Server created...waiting for client")
    try:
        server.bind((host, port))
    except socket.error as msg:
        print(msg)
    print("[S] Socket bind complete.")
    server.listen(2)
    counter = 1

    # Use while loop to ensure that the server is always running and waiting for a client to connect
    while True:
        clientSocket, clientAddress = server.accept()
        print('[S] Client connected')

        # filePath = r"C:\_receiveMDPimage\image" + str(counter) + ".jpg"
        filePath = r"/objectDetection/_receiveMDPimage/image" + str(counter) + ".jpg"
        # filePath = r"C:\_receiveMDPimage\testImage.jpg"
        # filePath = r"/Users/sizzlingzf/Documents/y3s1/CZ3004 MDP/yolov5/data/images/image" + str(counter) + ".jpg"
        file = open(filePath, 'wb')

        # Receive image from client in byte chunks and writing jpg image in file path
        imageChunk = clientSocket.recv(2048)
        while imageChunk:
            file.write(imageChunk)
            if not imageChunk:
                break
            else:
                imageChunk = clientSocket.recv(2048)
        print('[S] Image received')
        file.close()

        # Calling command line to run detect.py
        os.chdir("/objectDetection/yolov5")
        os.system('python detect.py --weights best.pt --img 416 --conf 0.1 --source images')
        # os.chdir("/Users/sizzlingzf/Documents/y3s1/CZ3004 MDP/yolov5")
        # os.system(
        #     'python detect.py --source data/images/image' + str(
        #         counter) + '.jpg --weights best2.pt --img 640 --device 0' +
        #     '--save-conf --hide-conf --name results --exist-ok --save-txt')
        # reconnecting client
        clientSocket, clientAddress = server.accept()
        labelText = "/objectDetection/yolov5/runs/detect/results/labels/image" + str(
            counter) + ".txt"
        # labelText = "/Users/sizzlingzf/Documents/y3s1/CZ3004 MDP/yolov5/runs/detect/results/labels/image" + str(
        #     counter) + ".txt"
        

        # Check if image saved has corresponding label file. If yes, send string message of label back to RPI. Else, send error message.
        if path.exists(labelText) is True:
            with open(labelText, 'r') as f:
                data = f.read()
            result = str(data)				#result is the label of image
            label = int(result[:2].strip()) + 1
            message = str(label)
            print('[S] Sending: ' + message)
            clientSocket.send(message.encode('utf-8'))
            print("[S] Message sent.")
            time.sleep(1)
        else:
            message = "[S] No image detected."
            clientSocket.send(message.encode('utf-8'))
            print("[S] Error message sent.")
            time.sleep(1)
        counter += 1


if __name__ == '__main__':
    setupServer()
