package fi.solita.botsofbf.dto;

public class Wall {

    public final Position upperLeftCorner;
    public final int width;
    public final int height;

    public Wall(Position upperLeftCorner, int width, int height) {
        this.upperLeftCorner = upperLeftCorner;
        this.width = width;
        this.height = height;
    }

    public boolean containsPosition(Position pos) {
        return upperLeftCorner.x <= pos.x && pos.x < upperLeftCorner.x + width &&
                upperLeftCorner.y <= pos.y && pos.y < upperLeftCorner.y + height;
    }

}
