import cv2
import numpy as np
from PIL import Image
from os import path


def imagestiching():
    h_list1 = []
    h_list2 = []
    fontScale = 2
    color = (0, 0, 255)
    font = cv2.FONT_HERSHEY_SIMPLEX
    org = (250,80)
    thickness = 4

    counter = 1 # to iterate through images
    x = 1 # to separate into 2 rows
    
    # stich images 1 to 10
    while counter < 11:
        imagepath = r"C:\Users\acer\COMBINEDCODE\yolov5\images\image" + str(counter) + ".jpg"

        if path.exists(imagepath) is True:
            image = cv2.imread(imagepath)
            image = cv2.resize(image, (750, 750))
            image = cv2.putText(image, 'IMAGE ' + str(counter), org, font, fontScale, color, thickness, cv2.LINE_AA)
            if x <= 5:
                h_list1.append(image)
                x += 1
            else:
                h_list2.append(image)
        else:
            blankimage = Image.new("RGB", (750, 750), (255, 255, 255))
            blankimage.save(imagepath)
            image = cv2.imread(imagepath)
            image = cv2.putText(image, 'IMAGE ' + str(counter), org, font, fontScale, color, thickness, cv2.LINE_AA)
            if x <= 5:
                h_list1.append(image)
            else:
                h_list2.append(image)
        counter += 1

    h_stack1 = np.hstack(h_list1)
    h_stack2 = np.hstack(h_list2)
    v_stack = np.vstack([h_stack1, h_stack2])

    # saving the collage to results
    outpath = r"C:\Users\acer\COMBINEDCODE\yolov5\runs\detect\results\collage.jpg"
    cv2.imwrite(outpath, v_stack)
    im = Image.open(outpath)
    im.show()
    exit(0)


if __name__ == "__main__":
    imagestiching()
