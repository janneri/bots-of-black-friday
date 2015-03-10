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

    public Position(int x, int y, Map map) {
        this.x = bound(x, 0, map.width);
        this.y = bound(y, 0, map.height);
    }

    public Position move(Move move, Map map) {
        switch (move) {
            case UP: return new Position(this.x, this.y - 10, map);
            case RIGHT: return new Position(this.x + 10, this.y, map);
            case LEFT: return new Position(this.x - 10, this.y, map);
            default: return new Position(this.x, this.y + 10, map);
        }
    }

    private int bound(int coord, int min, int max) {
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
}
