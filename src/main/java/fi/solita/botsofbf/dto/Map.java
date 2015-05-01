package fi.solita.botsofbf.dto;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Map {

    public final int width;
    public final int height;
    public final int maxItemCount;
    public final List<String> tiles;
    public final String name;
    public final Position exit;

    // todo create Tile type
    public static final char WALL = 'x';
    public static final char EXIT = 'o';
    public static final char FLOOR = ' ';

    private final List<Position> movablePositions;

    private Map(String name, int width, int height, int maxItemCount, List<String> tiles,
                Position exit, List<Position> movablePositions) {
        this.width = width;
        this.height = height;
        this.maxItemCount = maxItemCount;
        this.tiles = tiles;
        this.name = name;
        this.exit = exit;
        this.movablePositions = movablePositions;
    }

    public Position randomFloorPosition() {
        Random rand = new Random();
        return movablePositions.get(rand.nextInt(movablePositions.size()));
    }

    public boolean isMovablePosition(Position pos) {
        return pos.x >= 0 && pos.x <= width &&
               pos.y >= 0 && pos.y <= height &&
               !isWall(pos);
    }

    private boolean isWall(Position pos) {
        return tiles.get(pos.y).charAt(pos.x) == WALL;
    }

    public static Map readMapFromFile(String mapFileName) {
        try {
            Path path = Paths.get("/bobf-maps", mapFileName);
            if ( path.toFile().exists() ) {
                return readMapFromPath(path);
            } else {
                return readMapFromPath(Paths.get(ClassLoader.getSystemResource("maps/" + mapFileName).toURI()));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Reading map file " + mapFileName + " failed.", e);
        }
    }

    // todo map to Tile and use Stream::findFirst
    private static Position getPositionOf(List<String> lines, char tile) {
        for (int y = 0; y < lines.size(); y++) {
            for (int x = 0; x < lines.get(y).length(); x++) {
                if (lines.get(y).charAt(x) == tile) {
                    return Position.of(x, y);
                }
            }
        }
        throw new IllegalArgumentException("Position of " + tile + " not found");
    }

    // todo map to Tile and use Stream::filter
    private static List<Position> filterPositionsOfType(List<String> lines, char tile) {
        List<Position> positions = new ArrayList();
        for (int y = 0; y < lines.size(); y++) {
            for (int x = 0; x < lines.get(y).length(); x++) {
                if (lines.get(y).charAt(x) == tile) {
                    positions.add(Position.of(x, y));
                }
            }
        }
        return positions;
    }

    private static Map readMapFromPath(Path path) throws IOException {
        return createMapFromLines(Files.lines(path, Charset.forName("UTF-8")).collect(Collectors.toList()));
    }

    public static Map createMapFromLines(List<String> lines) {
        // header
        String mapName = lines.get(0);
        int maxItemCount = Integer.parseInt(lines.get(1));
        // tile rows
        List<String> tiles = lines.stream().skip(2).collect(Collectors.toList());
        int width = tiles.get(0).length();
        int height = tiles.size();
        System.out.println("w " + width + ", h " + height + ", name " + mapName + " items " + maxItemCount);

        final List<Position> floorTiles = filterPositionsOfType(tiles, FLOOR);
        if ( floorTiles.size() == 0) {
            throw new IllegalStateException("The map must contain movable tiles.");
        }

        return new Map(mapName, width, height, maxItemCount, tiles, getPositionOf(tiles, EXIT), floorTiles);
    }


}
