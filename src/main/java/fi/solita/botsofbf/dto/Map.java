package fi.solita.botsofbf.dto;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Map {

    public static final int DEFAULT_HEIGHT = 50;
    public static final int DEFAULT_WIDTH = 50;

    public final int width;
    public final int height;
    public final int maxItemCount;
    public final List<String> tiles;
    public final String name;

    private Map(String name, int width, int height, int maxItemCount, List<String> tiles) {
        this.width = width;
        this.height = height;
        this.maxItemCount = maxItemCount;
        this.tiles = tiles;
        this.name = name;
    }

    public static Map createDefault() {
        return readMapFromFile("default.map");
    }

    public Position randomValidPosition(Stream<Position> excludedPositions) {
        for (int i = 0; i < 100; i++) {
            Position pos = randomPosition();
            if ( isValidPosition(pos) && excludedPositions.noneMatch(p -> p.equals(pos))) {
                return pos;
            }
        }
        throw new IllegalStateException("Cannot find a valid random position.");
    }

    public Position randomPosition() {
        Random rand = new Random();
        return new Position(rand.nextInt(width), rand.nextInt(height));
    }

    public boolean isValidPosition(Position pos) {
        return pos.x >= 0 && pos.x <= width &&
               pos.y >= 0 && pos.y <= height &&
               !isWall(pos);
    }

    public boolean isWall(Position pos) {
        char tile = tiles.get(pos.y).charAt(pos.x);
        return tile == 'x';
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

    private static Map readMapFromPath(Path path) throws IOException {
        List<String> lines = Files.lines(path, Charset.forName("UTF-8")).collect(Collectors.toList());
        String mapName = lines.remove(0);
        int maxItemCount = Integer.parseInt(lines.remove(0));
        int width = lines.get(0).length();
        int height = lines.size();
        System.out.println("w " + width + ", h " + height + ", name " + mapName + " items " + maxItemCount);

        return new Map(mapName, width, height, maxItemCount, lines);
    }

}
