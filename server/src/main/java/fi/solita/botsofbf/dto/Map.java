package fi.solita.botsofbf.dto;

import org.apache.commons.collections4.SetUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
    public static final char FLOOR = '_';
    public static final char TRAP = '#';

    private final Set<Position> movablePositions;

    private Map(String name, int width, int height, int maxItemCount, List<String> tiles,
                Position exit, Set<Position> movablePositions) {
        this.width = width;
        this.height = height;
        this.maxItemCount = maxItemCount;
        this.tiles = tiles;
        this.name = name;
        this.exit = exit;
        this.movablePositions = movablePositions;
    }

    public Position randomFloorPosition(Set<Position> avoid) {
        final SetUtils.SetView<Position> freePositions = SetUtils.difference(movablePositions, avoid);

        if (freePositions.isEmpty()) {
            return null;
        }

        return freePositions.stream()
                .skip(new Random().nextInt(freePositions.size()))
                .findFirst()
                .orElse(null);
    }

    public Position randomFloorPosition() {
        return randomFloorPosition(Collections.emptySet());
    }

    public boolean isMovablePosition(Position pos) {
        return pos.x >= 0 && pos.x <= width &&
               pos.y >= 0 && pos.y <= height &&
               !isWall(pos);
    }

    private boolean isWall(Position pos) {
        return tiles.get(pos.y).charAt(pos.x) == WALL;
    }

    public boolean isTrap(Position pos) { return tiles.get(pos.y).charAt(pos.x) == TRAP; }

    public static Map readMapFromFile(String mapFileName) {
        try {
            Path path = Paths.get("/bobf-maps", mapFileName);
            if ( path.toFile().exists() ) {
                return createMapFromLines(Files.lines(path, Charset.forName("UTF-8")).collect(Collectors.toList()));
            } else {
                return createMapFromLines(readClasspathResourceAsLines(mapFileName));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Reading map file " + mapFileName + " failed.", e);
        }
    }

    private static List<String> readClasspathResourceAsLines(String mapFileName) throws IOException {
        try (final InputStream inputStream = Map.class.getClassLoader().getResourceAsStream("maps/" + mapFileName);
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
           return bufferedReader.lines().collect(Collectors.toList());
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
    private static Set<Position> filterPositionsOfType(List<String> lines, Set<Character> tiles) {
        Set<Position> positions = new HashSet<Position>();
        for (int y = 0; y < lines.size(); y++) {
            for (int x = 0; x < lines.get(y).length(); x++) {
                if (tiles.contains(Character.valueOf(lines.get(y).charAt(x)))) {
                    positions.add(Position.of(x, y));
                }
            }
        }
        return positions;
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

        final Set<Character> movable = new HashSet<>();
        movable.add(FLOOR);
        movable.add(TRAP);
        final Set<Position> floorTiles = filterPositionsOfType(tiles, movable);
        if (floorTiles.isEmpty()) {
            throw new IllegalStateException("The map does not have any floor tiles.");
        }
        if (floorTiles.size() < maxItemCount) {
            throw new IllegalStateException("The map does not have enough floor tiles for max items.");
        }

        return new Map(mapName, width, height, maxItemCount, tiles, getPositionOf(tiles, EXIT), floorTiles);
    }
}
