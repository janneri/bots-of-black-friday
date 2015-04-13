package fi.solita.botsofbf.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class Map {

    public static final int DEFAULT_HEIGHT = 50;
    public static final int DEFAULT_WIDTH = 50;

    public final int width;
    public final int height;
    public final int maxItemCount;
    public final List<Wall> walls;

    public Map(int width, int height, int maxItemCount, List<Wall> walls) {
        this.width = width;
        this.height = height;
        this.maxItemCount = maxItemCount;
        this.walls = walls;
    }

    public static Map createDefault() {
        return new Map(DEFAULT_WIDTH, DEFAULT_HEIGHT, 10, Arrays.asList());
    }

    public static Map siwa() {
        return new Map(30, 30, 5, Arrays.asList(
                new Wall(Position.of(5, 10), 5, 1),
                new Wall(Position.of(5, 19), 5, 1),
                new Wall(Position.of(10, 5), 15, 1),
                new Wall(Position.of(10, 24), 15, 1),
                new Wall(Position.of(10, 6), 1, 5),
                new Wall(Position.of(10, 19), 1, 5)));
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
               !walls.stream().anyMatch(w -> w.containsPosition(pos));
    }

}
