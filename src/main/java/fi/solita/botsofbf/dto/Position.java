package fi.solita.botsofbf.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Position {

    public final int x;
    public final int y;

    @JsonIgnore
    public static final Position ORIGIN =  new Position(0, 0);

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Position of(int x, int y) {
        return new Position(x, y);
    }

    public static Position boundedPos(int x, int y, int width, int height) {
        return new Position(bound(x, 0, width), bound(y, 0, height));
    }

    public Position move(Move move, int width, int height) {
        switch (move) {
            case UP: return boundedPos(this.x, this.y - 1, width, height);
            case RIGHT: return boundedPos(this.x + 1, this.y, width, height);
            case LEFT: return boundedPos(this.x - 1, this.y, width, height);
            default: return boundedPos(this.x, this.y + 1, width, height);
        }
    }

    private static int bound(int coord, int min, int max) {
        return Math.max(Math.min(coord, max), min);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (x != position.x) return false;
        if (y != position.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
