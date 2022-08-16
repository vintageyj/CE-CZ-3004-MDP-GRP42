package ntu.mdp.grp42.arena;

import android.content.Context;

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
}
