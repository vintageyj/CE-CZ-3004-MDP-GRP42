package ntu.mdp.grp42.bluetooth;

public interface RaspberryPiProtocol {
    // Robot Movement Controls
    String FORWARD = "w";
    String REVERSE = "reverse";
    String LEFT_TURN = "q";
    String RIGHT_TURN = "e";

    // Spawn Arena Objects
    String ADD_OBSTACLE = "POS";
    String REMOVE_OBSTACLE = "remove_obstacle";
    String UPDATE_OBSTACLE = "update_obstacle";
    String SPAWN_ROBOT = "spawn_robot";
    String ROTATE_ROBOT = "rotate_robot";

    // Task Controls
    String START_TASK1 = "start_task_1";
    String START_TASK2 = "start_task_2";
    String STOP_TASK = "stop_task";

    // Update Status Window
    String PREDICTED_PATH = "predicted_path";
    String STATUS = "status";
    String CONNECTION = "C";
    String RPI = "R";
    String PC = "A";
    String STM = "S";
}
