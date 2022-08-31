package ntu.mdp.grp42.arena;

import android.content.Context;

import ntu.mdp.grp42.R;

public class ArenaCell extends androidx.appcompat.widget.AppCompatButton {
    public int x, y, obstacleID = -1;

    public ArenaCell(Context context) {
        super(context);
    }

    public ArenaCell(Context context, int x, int y) {
        super(context);
        this.x = x;
        this.y = y;
    }

    public int getImageID(int imageID) {
        switch (imageID) {
            case 11:
                return R.drawable.id_11;
            case 12:
                return R.drawable.id_12;
            case 13:
                return R.drawable.id_13;
            case 14:
                return R.drawable.id_14;
            case 15:
                return R.drawable.id_15;
            case 16:
                return R.drawable.id_16;
            case 17:
                return R.drawable.id_17;
            case 18:
                return R.drawable.id_18;
            case 19:
                return R.drawable.id_19;
            case 36:
                return R.drawable.id_36;
            case 37:
                return R.drawable.id_37;
            case 38:
                return R.drawable.id_38;
            case 39:
                return R.drawable.id_39;
            case 40:
                return R.drawable.id_40;
            default:
                return R.drawable.dummy_obstacle;
        }
    }
}
