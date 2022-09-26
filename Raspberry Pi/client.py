# on RPI
# sends PC RPI image taken
# receives image recognised from RPI
# sends STM the info

import socket
import picamera
import time
import sys

host = '10.27.150.156'  # The server's hostname or IP address
port = 54321  # The port used by the server


def captureImage():
    # Generate the picture's name
    print('[RPI_INFO] Initializing Camera.')
    camera = picamera.PiCamera()
    camera.resolution = (2592, 1944)
    camera.framerate = 30
    # camera.vflip = True
    # camera.hflip = True
    camera.brightness = 55
    camera.start_preview()

    print('[RPI_INFO] Warming up camera...')
    print('[RPI_INFO] Camera warmed up and ready')

    picName = 'image.jpg'
    picPath = "/home/pi/MDP/sendImages/"
    completePath = picPath + picName
    camera.capture(completePath)
    print("We have taken a picture.")
    camera.stop_preview()
    camera.close()
    return completePath


def msgClient():
    newClient = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    newClient.connect((host, port))
    print('Client 2 connected to server')
    message = newClient.recv(1024)
    message = message.decode('utf-8')
    print('Received: ' + str(message))  
    # send to android
    newClient.close()
    print('Client 2 received message')


def imageClient():
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client.connect((host, port))
    print('Client 1 connected to server')

    path = captureImage()
    image = open(path, 'rb')
    imageData = image.read(2048)
    while imageData:
        client.send(imageData)
        imageData = image.read(2048)
    print('Finished sending image')
    image.close()
    client.close()
    print('Client 1 closed')
    time.sleep(3)
    msgClient()


if __name__ == "__main__":
    imageClient()
    sys.exit(0)
