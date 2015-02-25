package fi.solita.botsofbf.dto;

public class Position {

    private static final int MAP_HEIGHT = 800;
    private static final int MAP_WIDTH = 800;

    public static final Position ORIGIN = new Position(0, 0);

    public final int x;
    public final int y;

    public Position(int x, int y) {
        this.x = bound(x, 0, MAP_WIDTH);
        this.y = bound(y, 0, MAP_HEIGHT);
    }

    public Position move(Move move) {
        switch (move) {
            case UP: return new Position(this.x, this.y - 10);
            case RIGHT: return new Position(this.x + 10, this.y);
            case LEFT: return new Position(this.x - 10, this.y);
            default: return new Position(this.x, this.y + 10);
        }
    }

    private int bound(int coord, int min, int max) {
        return Math.max(Math.min(coord, max), min);
    }


}
