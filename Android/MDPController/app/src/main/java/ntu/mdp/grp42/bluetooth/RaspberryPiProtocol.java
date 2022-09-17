package ntu.mdp.grp42.bluetooth;

public interface RaspberryPiProtocol {
    // Robot Movement Controls
    String FORWARD = "forward";
    String REVERSE = "reverse";
    String LEFT_TURN = "left_turn";
    String RIGHT_TURN = "right_turn";

    // Spawn Arena Objects
    String ADD_OBSTACLE = "add_obstacle";
    String REMOVE_OBSTACLE = "remove_obstacle";
    String UPDATE_OBSTACLE = "update_obstacle";
    String SPAWN_ROBOT = "spawn_robot";
    String ROTATE_ROBOT = "rotate_robot";

    // Task Controls
    String START_TASK1 = "start_task_1";
    String START_TASK2 = "start_task_2";
    String STOP_TASK = "stop_task";

    // Update Status Window
    String STATUS = "status";
    String CONNECTION = "connection_ok";
    String RPI = "rpi";
    String PC = "pc";
    String STM = "stm";
}
