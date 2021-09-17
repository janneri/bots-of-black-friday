package fi.solita.botsofbf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Position {
    public int x;
    public int y;

    public Position(@JsonProperty("x") int x, @JsonProperty("y") int y) {
        this.x = x;
        this.y = y;
    }

    public Position move(Move move) {
        if (move == Move.UP) {
            return new Position(x, y - 1);
        }
        else if (move == Move.DOWN) {
            return new Position(x, y + 1);
        }
        else if (move == Move.LEFT) {
            return new Position(x - 1, y);
        }
        else if (move == Move.RIGHT) {
            return new Position(x + 1, y);
        }
        else {
            return this;
        }
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}
