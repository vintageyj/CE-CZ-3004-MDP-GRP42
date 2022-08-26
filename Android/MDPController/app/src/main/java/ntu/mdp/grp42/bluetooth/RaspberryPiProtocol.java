package ntu.mdp.grp42.bluetooth;

public interface RaspberryPiProtocol {
    // Robot movement controls
    String FORWARD = "forward";
    String REVERSE = "reverse";
    String LEFT_TURN = "left_turn";
    String RIGHT_TURN = "right_turn";

    // Spawn Arena Objects
    String ADD_OBSTACLE = "add_obstacle";
    String REMOVE_OBSTACLE = "remove_obstacle";
    String SPAWN_ROBOT = "spawn_robot";
    String ROTATE_ROBOT = "rotate_robot";

}