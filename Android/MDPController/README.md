# Android Remote Controller Module
The **Android Remote Controller Module** is an Android application that provides a functional GUI to enable interactions between the user and Robot Car through.


## Checklist Requirements
- [x] The Android application is able to transmit and receive text strings over the Bluetooth serial communication link
- [x] Functional graphical user interface (GUI) that is able to initiate the scanning, selection and connection with a Bluetooth device
- [x] Functional GUI that provides interactive control of the robot movement via the Bluetooth link
- [x] Functional GUI that shows remote update & status messages
- [x] 2D display of the exploration arena with obstacles and the robot's location
- [x] Interactive movement and placement of obstacles in map
  - [x] Place the square obstacles into the map through touch interactions
  - [x] Obstacles in the map are movable through "touch and drag" interaction
  - [x] Dragging and dropping an obstacle outside the map area will remove the obstacle from the map
  - [x] Once the positioning of the obstacle is completed, its coordinate and assigned obstacle number is updated on the Bluetooth channel
- [x] Interactive annotation of the face of the obstacle where the target image is located
- [x] Robust connectivity with Bluetooth device
- [x] Displaying Image Target ID on Obstacle Blocks in the Map
- [x] Updating Position and Facing Direction of Robot in the Map

## Backlogs 
- [x] Buttons for initiating Task 1 & 2 and their timers
- [x] Update status window
- [x] Logic for updating recognised images
- [x] Setup Bluetooth Features
  - [x] On & Off
  - [x] Enable device discoverability
  - [x] Scan for new devices and query for paired devices
  - [x] Bluetooth String Communication (READ & WRITE)
- [x] Arena Features
