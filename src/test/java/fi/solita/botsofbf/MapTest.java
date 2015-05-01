package fi.solita.botsofbf;


import fi.solita.botsofbf.dto.Map;
import fi.solita.botsofbf.dto.Position;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MapTest {

    private List<String> mapLines = Arrays.asList(
            "halpahalli",
            "5",
            "xxxxxxx",
            "x__o__x ",
            "xxxxxxx");

    @Test
    public void parseMapName() {
        Map map = Map.createMapFromLines(mapLines);
        assertEquals("halpahalli", map.name);
    }

    @Test
    public void parseMaxItemCount() {
        Map map = Map.createMapFromLines(mapLines);
        assertEquals(5, map.maxItemCount);
    }

    @Test
    public void wallDetection() {
        Map map = Map.createMapFromLines(mapLines);
        List<Position> walls = Arrays.asList(Position.of(0, 0), Position.of(0, 1), Position.of(1, 0));
        List<Position> floor = Arrays.asList(Position.of(1, 1), Position.of(2, 1), Position.of(4, 1));

        assertTrue(walls.stream().allMatch(pos -> !map.isMovablePosition(pos)));
        assertTrue(floor.stream().allMatch(pos -> map.isMovablePosition(pos)));
    }

    @Test
    public void randomMovablePosition() {
        Map map = Map.createMapFromLines(mapLines);
        List<Position> positions = IntStream.range(0, 20).boxed()
                .map(n -> map.randomFloorPosition())
                .collect(Collectors.toList());
        Set<Position> uniquePositions = new HashSet<>(positions);

        assertTrue(positions.stream().allMatch(pos -> map.isMovablePosition(pos)));
        assertTrue(uniquePositions.size() > 2);
    }

}