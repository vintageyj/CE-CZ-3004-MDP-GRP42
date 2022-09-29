# on PC
# receives RPI images
# runs detect.py for image recognition on image received
# sends RPI info on the image detected

# import os
# import socket
# import time
# import os.path
# from os import path

# host = '10.27.248.240'
# port = 54321

# def setupServer():
#     server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#     print("[S] Server created...waiting for client")
#     try:
#         server.bind((host, port))
#     except socket.error as msg:
#         print(msg)
#     print("[S] Socket bind complete.")
#     server.listen(2)
#     counter = 1

#     # Use while loop to ensure that the server is always running and waiting for a client to connect
#     while True:
#         clientSocket, clientAddress = server.accept()
#         print('[S] Client connected')

#         # creating image file
#         filePath = r"/objectDetection/yolov5/images/image" + str(counter) + ".jpg"
#         file = open(filePath, 'wb')

#         # receive image from client in byte chunks and writing to image file created
#         imageChunk = clientSocket.recv(2048)
#         while imageChunk:
#             file.write(imageChunk)
#             if not imageChunk:
#                 break
#             else:
#                 imageChunk = clientSocket.recv(2048)
#         print('[S] Image received')
#         file.close()

#         # calling command line to run detect.py for image detection
#         os.chdir("/objectDetection/yolov5")
#         os.system('python detect.py --weights best.pt --source images --img 416 --conf 0.3 --save-conf --hide-conf --name results --exist-ok --save-txt')

#         # reconnecting client
#         clientSocket, clientAddress = server.accept()

#         # check if image saved has corresponding label file
#         labelText = "/objectDetection/yolov5/runs/detect/results/labels/image" + str(counter) + ".txt"
#         # if yes, send string message of label back to RPI
#         # else, send error message.
#         if path.exists(labelText) is True:
#             with open(labelText, 'r') as f:
#                 data = f.read()
#             result = str(data)  
#             label = int(result[:2].strip()) + 1
#             message = str(label)
#             print('[S] Sending: ' + message)
#             clientSocket.send(message.encode('utf-8'))
#             print("[S] Message sent.")
#             time.sleep(1)
#         else:
#             message = "[S] No image detected."
#             clientSocket.send(message.encode('utf-8'))
#             print("[S] Error message sent.")
#             time.sleep(1)
#         counter += 1


# if __name__ == '__main__':
#     setupServer()


# on PC
# receives RPI images
# runs detect.py for image recognition on image received
# sends RPI info on the image detected

import os
import socket
import time
import os.path
from os import path
import _thread

host = '192.168.42.12'
port = 54321
port_image = 55555
folder_name = "C:\\Users\\Dayou\\Documents\\MDP\\COMBINEDCODE"
algo_folder_name = "C:\\Users\\Dayou\\IdeaProjects\\MDPALGO"


class Server():
    def send_path_txt_file_algo(self, path_str):
        # f = open(
        #     r"C:\Users\acer\COMBINEDCODE\pathFromRPI.txt", 'w')
        f = open(
            algo_folder_name + "\pathFromRPI.txt", 'w')
        print(path_str)
        f.write(path_str)
        f.close()

    def listen_for_algo(self):
        # function to listen for instructions for algo side
        while True:
            try:
                message = self.algo_clientSocket.recv(1024)
                #print("Try getting message")
                # time.sleep(1)
                if (not len(message) > 0):
                    continue
            except:
                continue
            message = message.decode('utf-8')
            print("received: " + message + "!")
            instr = message.split()
            if (len(message) == 0):
                print("RECEIVED EMPTINESS")
                continue
            print("===RUN ALGO CODE===")

            # write the obstacle path received from android to pathFromRPI
            self.send_path_txt_file_algo(instr[1])

            print("===READ FROM ALGO OUTPUT FILE FOR STM===")
            # update algo file
            #algoOutput = r"C:\Users\acer\COMBINEDCODE\CommandsForSTM.txt"
            algoOutput = algo_folder_name + "\CommandsForSTM.txt"
            with open(algoOutput, 'r') as f:
                data = f.read()
            result = 'S ' + str(data)
            print('SENDING: ' + result)
            self.algo_clientSocket.send(result.encode('utf-8'))

            print("===READ FROM ALGO OUTPUT FILE FOR ANDROID===")
            #algoOutput = r"C:\Users\acer\COMBINEDCODE\PathForAndroid.txt"
            algoOutput = algo_folder_name + "\PathForAndroid.txt"
            with open(algoOutput, 'r') as f:
                data = f.read()
            result = "predicted_path "+str(data)
            print('SENDING: ' + result)
            self.algo_clientSocket.send(result.encode('utf-8'))

    def listen_for_image(self):
        def task2detection(labelText):
            with open(labelText, 'r') as f:
                result=[line.rstrip() for line in f] # each line is stored in the array

            for lines in result:
                parts = lines.split() # splits up each component within a line and stores in array
                if (parts[0]=='27' or parts[0]=='28'):  # only return if arrow
                    return(str(int(parts[0])+11))

        # function to listen for instructions for image processing
        counter = 1

        while True:
            try:
                message = self.clientSocket_image.recv(1024)
                if (not len(message) > 0):
                    continue
            except:
                continue
            message = message.decode('utf-8')
            print("received: " + message + "!")
            if (len(message) == 0):
                print("RECEIVED EMPTINESS")
                continue
            taskno=message
            # creating image file
            # filePath = r"C:\Users\acer\COMBINEDCODE\yolov5\images\image" + \
            #     str(counter) + ".jpg"
            filePath = folder_name + "\yolov5\images\image" + \
                str(counter) + ".jpg"
            file = open(filePath, 'wb')
            imageChunk = self.clientSocket_image.recv(4096)
            # receive image from client in byte chunks and writing to image file created
            self.clientSocket_image.settimeout(0.5)
            while (True):
                try:
                    file.write(imageChunk)
                    #print("trying to get image")
                    imageChunk = self.clientSocket_image.recv(4096)
                    #print("got image")
                except:
                    break

                if not imageChunk:
                    break

            self.clientSocket_image.settimeout(None)
            print('===IMAGE RECEIVED===')
            file.close()

            print("===RUN IMAGE RECOGNITION===")
            # calling command line to run detect.py for image recognition
            # os.chdir(r"C:\Users\acer\COMBINEDCODE\yolov5")
            os.chdir(folder_name + "\yolov5")
            # os.system(
            #     'python detect.py --weights best.pt --source images --img 416 --conf 0.3 --save-conf --hide-conf --name results --exist-ok --save-txt')
            os.system('python detect.py --weights best.pt --img 416 --conf 0.3 --save-conf --hide-conf --name results --exist-ok --save-txt --device 0 --source images/image'+str(counter)+".jpg")

            # reconnecting client
            #clientSocket_image, clientAddress = server_image.accept()

            # sending image recognition results to RPI
            # labelText = r"C:\Users\acer\COMBINEDCODE\yolov5\runs\detect\results\labels\image" + str(
            #     counter) + ".txt"
            labelText = folder_name + "\\yolov5\\runs\\detect\\results\\labels\\image" + str(
                counter) + ".txt"
            print("LabelText: " + labelText)
            
            if path.exists(labelText) is True:
                if (taskno=='I1'):
                    with open(labelText, 'r') as f:
                        data = f.read()
                    result = str(data)
                    label = int(result[:2].strip()) + 11
                    message = str(label)
                elif (taskno=='I2'):
                    message = task2detection(labelText)
                else:
                    message = "invalid task number"
                print('SENDING: ' + message)
                self.clientSocket_image.send(message.encode('utf-8'))
                print("===MESSAGE SENT===")
                time.sleep(1)
            else:
                message = "I -"
                self.clientSocket_image.send(message.encode('utf-8'))
                print("===NO IMAGE DETECTED===")
                time.sleep(1)

            counter += 1

    def algo_connect_rpi(self):
        self.algo_server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        print("===SERVER CREATED, WAITING FOR CLIENT (ALGO)===")
        try:
            self.algo_server.bind((host, port))
        except socket.error as msg:
            print(msg)

        print("===SOCKET BIND (ALGO) COMPLETE===")
        self.algo_server.listen(2)
        self.algo_clientSocket, self.algo_clientAddress = self.algo_server.accept()
        print("===ALGO CLIENT CONNECTED===")
        self.algo_clientSocket.settimeout(2)

    def image_connect_rpi(self):
        self.server_image = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        print("===SERVER CREATED, WAITING FOR CLIENT (IMAGE)===")
        try:
            self.server_image.bind((host, port_image))
        except socket.error as msg:
            print(msg)

        print("===SOCKET BIND (IMAGE) COMPLETE===")
    
        self.server_image.listen(2)
        self.clientSocket_image, self.clientAddress_image = self.server_image.accept()
        print("===IMAGE CLIENT CONNECTED===")


if __name__ == '__main__':
    server = Server()
    connect_algo = _thread.start_new_thread(server.algo_connect_rpi, ())
    connect_image = _thread.start_new_thread(server.image_connect_rpi, ())
    algo_thread = _thread.start_new_thread(server.listen_for_algo, ())
    image_thread = _thread.start_new_thread(server.listen_for_image, ())
    while True:
        pass
    print("Ended server")
