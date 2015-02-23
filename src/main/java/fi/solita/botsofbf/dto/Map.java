package fi.solita.botsofbf.dto;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Map {

    public final int width;
    public final int height;
    public final String cells;

    //public static final Map SMALL = new Map("small_map.txt");

    public Map(int width, int height, String cells) {
        this.width = width;
        this.height = height;
        this.cells = cells;
    }

    public static Map fromFile(String mapFilePath) throws IOException {

        Stream<String> lines = Files.lines(Paths.get(URI.create(mapFilePath)));
        int width = lines.findFirst().get().length();
        int height = (int) lines.count();

        String cells = lines
                //.map(i -> i.toString())
                .collect(Collectors.joining());

        return new Map(width, height, cells);
    }
}
