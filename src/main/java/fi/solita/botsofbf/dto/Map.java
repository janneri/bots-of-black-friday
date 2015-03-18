package fi.solita.botsofbf.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Map {

    public static final int DEFAULT_HEIGHT = 600;
    public static final int DEFAULT_WIDTH = 800;

    public final int width;
    public final int height;
    public final List<Wall> walls;

    public Map(int width, int height, List<Wall> walls) {
        this.width = width;
        this.height = height;
        this.walls = walls;
    }

    public static Map createDefault() {
        return new Map(DEFAULT_WIDTH, DEFAULT_HEIGHT, Arrays.asList());
    }

    public static Map siwa() {
        return new Map(300, 300, Arrays.asList(new Wall(Position.of(50, 0), Position.of(60,50))));
    }

    public Position randomPosition() {
        return new Position(randInt(0, width), randInt(0, height));
    }

    public boolean isValidPosition(Position pos) {
        return pos.x >= 0 && pos.x <= width &&
               pos.y >= 0 && pos.y <= height &&
               !walls.stream().anyMatch(w -> w.containsPosition(pos));
    }

    private static int randInt(int min, int max) {
        Random rand = new Random();
        return roundUp(rand.nextInt((max - min) + 1) + min, 10);
    }

    private static int roundUp(double i, int v){
        return (int)Math.round(i/v) * v;
    }
}
