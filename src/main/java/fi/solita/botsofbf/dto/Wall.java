package fi.solita.botsofbf.dto;

public class Wall {

    public final Position upperleftCorner;
    public final Position lowerRightCorner;

    public Wall(Position upperleftCorner, Position lowerRightCorner) {
        this.upperleftCorner = upperleftCorner;
        this.lowerRightCorner = lowerRightCorner;
    }

    public boolean containsPosition(Position pos) {
        return upperleftCorner.x <= pos.x &&
                upperleftCorner.y <= pos.y &&
                lowerRightCorner.x >= pos.x &&
                lowerRightCorner.y >= pos.y;
    }

}
