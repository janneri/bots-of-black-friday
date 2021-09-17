package fi.solita.botsofbf.dto;

import java.util.List;

public class GameMap {
    public int width;
    public int height;
    public List<String> tiles;

    public boolean isValidPosition(Position pos) {
        return pos.x >= 0 && pos.x <= width && pos.y >= 0 && pos.y <= height && !isWall(pos);
    }

    public boolean isWall(Position pos) {
        char tile = tiles.get(pos.y).charAt(pos.x);
        return tile == Tiles.WALL;
    }
}
