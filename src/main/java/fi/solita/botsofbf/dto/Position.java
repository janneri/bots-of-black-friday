package fi.solita.botsofbf.dto;

public class Position {

    public static final Position ORIGIN = new Position(0, 0);

    public final int x;
    public final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position move(Move move) {
        switch (move) {
            case UP: return new Position(this.x, this.y - 1);
            case RIGHT: return new Position(this.x + 1, this.y);
            case LEFT: return new Position(this.x - 1, this.y);
            default: return new Position(this.x, this.y + 1);
        }
    }

}
