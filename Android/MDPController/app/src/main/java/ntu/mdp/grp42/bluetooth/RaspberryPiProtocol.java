package ntu.mdp.grp42.bluetooth;

public interface RaspberryPiProtocol {
    // Robot Movement Controls
    String FORWARD = "f";
    String REVERSE = "b";
    String LEFT_TURN = "q";
    String RIGHT_TURN = "e";

    // Spawn Arena Objects
    String ADD_OBSTACLE = "POS";
    String REMOVE_OBSTACLE = "remove_obstacle";
    String UPDATE_OBSTACLE = "update_obstacle";
    String SPAWN_ROBOT = "SPAWN";
    String ROTATE_ROBOT = "rotate_robot";

    // Task Controls
    String START_TASK1 = "start_task_1";
    String START_TASK2 = "start_task_2";
    String STOP_TASK = "stop_task";
    String TAKE_PHOTO = "take_image";

    // Update Status Window
    String ALGO_INSTRUCTION = "algo";
    String PREDICTED_PATH = "predicted_path";
    String STATUS = "status";
    String CONNECTION = "C";
    String HOW = "HOW";
    String RPI = "R";
    String PC = "A";
    String STM = "S";

    // RTSP Stream Link
    String RTSP_LINK = "rtsp://192.168.42.42:8554/stream";
}
