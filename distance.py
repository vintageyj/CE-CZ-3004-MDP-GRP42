import os

def loadData():
    myDict = {}
    my_path = os.path.abspath(os.path.dirname(__file__))

    
    dir = os.path.join(my_path, "calibrationSet")
    fileNames = os.listdir(dir)
    for fileName in fileNames:
        path = os.path.join(my_path, "calibrationSet/" + fileName)
        file = open(path)
        lines = file.readlines()
        file.close()
        line = lines[0].split()
        width = float(line[3])
        height = float(line[4])
        myDict[fileName.split(".txt")[0]] = (width**2 + height**2)**0.5
        
    return myDict


def getDist(length, width):

    # Corner to corner diagonal length (Works with area of box too)
    xList = []

    # Distance between camera and image
    yList = []

    myDict = loadData()
    for i in myDict.keys():
        yList.append(int(i))

    yList = sorted(yList)
    
    for j in yList:
        xList.append(myDict[str(j)])

    x = (length**2 + width**2)**0.5

    # Assume that extrapolation is for further than calibrated range
    extrapolateConstant = xList[-1] * yList[-1]
    
    # If its closer than calibrated range, extrapolate from closer end
    if x > xList[0]:
        extrapolateConstant = xList[0] * yList[0]

    # By default, we use extrapolation
    distance = extrapolateConstant / x

    # If x is within range, we use interpolation instead
    if x <= xList[0] and x >= xList[-1]:
        for i in range(len(xList)):
            if xList[i] == x:
                distance = yList[i]
                break
            elif i > 0:
                if xList[i-1] > x and x > xList[i]:
                    y1 = yList[i-1]
                    y2 = yList[i]
                    x1 = xList[i-1]
                    x2 = xList[i]
                    distance = y1 + (x-x1) * (y2-y1) / (x2-x1)
                    break
    return distance

# for 60 and 130
print("\nFor 60 & 130:")
print(getDist(0.0226563, 0.0473773))
print(getDist(0.0421875, 0.0862944))

# for 130 and 200
print("\nFor 130 & 200:")
print(getDist(0.0226563, 0.0473773))
print(getDist(0.0148437, 0.0304569))

# for 150 and 220
print("\nFor 150 & 220:")
print(getDist(0.0140625, 0.0304569))
print(getDist(0.0195312, 0.0406091))