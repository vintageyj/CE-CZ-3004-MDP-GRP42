package ntu.mdp.grp42.bluetooth;

public interface Constants {
    // robot facing directions
    int UP = 0;
    int RIGHT = 1;
    int DOWN = 2;
    int LEFT = 3;

    // Message types sent from the BluetoothChatService Handler
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;

    // Bluetooth Connection Indicator
    String NOT_CONNECTED = "Not Connected";
    String CONNECTING = "Connecting";
    String CONNECTED = "Connected";


    // Key names received from the BluetoothChatService Handler
    String DEVICE_NAME = "device_name";
    String TOAST = "toast";

    // Bluetooth Reconnection Window
    int RECONNECTION_LIMIT = 30; // In seconds

    // Final Results
    String FINAL_RESULTS = "stitch";
}
