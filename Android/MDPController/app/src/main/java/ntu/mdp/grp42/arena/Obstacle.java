package ntu.mdp.grp42.arena;

public class Obstacle {
    private int x, y, direction, cellID, obstacleID;

    public Obstacle(int obstacleID, int cellID, int x, int y, int direction) {
        this.obstacleID = obstacleID;
        this.cellID = cellID;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }
}
