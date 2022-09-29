import math

def getBackDist():

    return

def getFrontDist():

    return

def inferLeftOrRight():

    return

def forward(travelDist):

    return

def forwardLeft(angleToReach, travelDist):

    return

def forwardRight(angleToReach, travelDist):

    return

def phase1(leftOrRight):
    x1 = getBackDist()
    x2 = getFrontDist()

    if leftOrRight == "left":
        lateral_dist = 20
        turnAngle = math.atan(lateral_dist / x2) * 180
        forwardLeft(turnAngle, math.sqrt(lateral_dist**2 + x2**2))
        forwardRight(turnAngle, 10)
    else:
        lateral_dist = 20
        turnAngle = math.atan(lateral_dist / x2) * 180
        forwardRight(turnAngle, math.sqrt(lateral_dist**2 + x2**2))
        forwardLeft(turnAngle, 10)
    return [x1, x2, inferLeftOrRight()]

def phase2(wasLeftOrRight, leftOrRight):
    x3 = getFrontDist()
    forwardDist = 5
    forward(forwardDist)
    if wasLeftOrRight == "left":
        if leftOrRight == "left":
            lateral_dist = 20
            turnAngle = math.atan(lateral_dist / (x3-forwardDist)) * 180
            forwardLeft(turnAngle, math.sqrt(lateral_dist**2 + (x3-forwardDist)**2))
            forwardRight(turnAngle, 10)
        else:
            lateral_dist = 50
            turnAngle = math.atan(lateral_dist / (x3-forwardDist)) * 180
            forwardRight(turnAngle, math.sqrt(lateral_dist**2 + (x3-forwardDist)**2))
            forwardLeft(turnAngle, 10)
    else:
        if leftOrRight == "left":
            lateral_dist = 50
            turnAngle = math.atan(lateral_dist / (x3-forwardDist)) * 180
            forwardLeft(turnAngle, math.sqrt(lateral_dist**2 + (x3-forwardDist)**2))
            forwardRight(turnAngle, 10)
        else:
            lateral_dist = 20
            turnAngle = math.atan(lateral_dist / (x3-forwardDist)) * 180
            forwardRight(turnAngle, math.sqrt(lateral_dist**2 + (x3-forwardDist)**2))
            forwardLeft(turnAngle, 10)

    return x3


def phase3(wasLeftOrRight, x2, x3):

    lateral_dist = 35
    if wasLeftOrRight == "left":
        forwardRight(90, 70)
        forwardRight(90, 10)
        turnAngle = math.atan(lateral_dist / (x2 + x3 + 10)) * 180
        forwardRight(turnAngle, math.sqrt(lateral_dist**2 + (x2 + x3 + 10)**2))
        forwardLeft(turnAngle, 15)

    else:
        forwardLeft(90, 70)
        forwardLeft(90, 10)
        turnAngle = math.atan(lateral_dist / (x2 + x3 + 10)) * 180
        forwardLeft(turnAngle, math.sqrt(lateral_dist**2 + (x2 + x3 + 10)**2))
        forwardRight(turnAngle, 15)

    return





def fastestCar(start):
    leftOrRight1 = inferLeftOrRight()

    if start:
        # Phase 1 (Check for the distance between inner wall of carpark and obstacle 1)
        phase1List = phase1(leftOrRight1)
        x1 = phase1List[0]
        x2 = phase1List[1]
        leftOrRight2 = phase1List[2]

        # Phase 2 (Check for the distance between obstacle 1 and obstacle 2)
        x3 = phase2(leftOrRight1, leftOrRight2)

        # Phase 3 (Homecoming)
        phase3(leftOrRight2, x2, x3)

    return True