package fi.solita.botsofbf;


import fi.solita.botsofbf.dto.Map;
import fi.solita.botsofbf.dto.Position;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapTest {

    private final List<String> mapLines = Arrays.asList(
            "halpahalli",
            "4",
            "xxxxxxx",
            "x__o__x ",
            "xxxxxxx");

    private final Set<Position> mapMovablePositions = Set.of(
            Position.of(1, 1),
            Position.of(2, 1),
            Position.of(4, 1),
            Position.of(5, 1));

    @Test
    public void parseMapName() {
        Map map = Map.createMapFromLines(mapLines);
        assertEquals("halpahalli", map.name);
    }

    @Test
    public void parseMaxItemCount() {
        Map map = Map.createMapFromLines(mapLines);
        assertEquals(4, map.maxItemCount);
    }

    @Test
    public void wallDetection() {
        Map map = Map.createMapFromLines(mapLines);
        List<Position> walls = Arrays.asList(Position.of(0, 0), Position.of(0, 1), Position.of(1, 0));

        assertTrue(walls.stream().noneMatch(map::isMovablePosition));
        assertTrue(mapMovablePositions.stream().allMatch(map::isMovablePosition));
    }

    @Test
    public void randomMovablePosition() {
        Map map = Map.createMapFromLines(mapLines);
        Position randomPosition = map.randomFloorPosition();

        assertTrue(map.isMovablePosition(randomPosition));
        assertThat(mapMovablePositions).contains(randomPosition);
    }

    @Test
    public void randomMovablePositionShouldReturnNullIfNoneFound() {
        Map map = Map.createMapFromLines(mapLines);

        assertThat(map.randomFloorPosition(mapMovablePositions)).isNull();
    }

    @Test
    public void creatingMapWithLessFloorSpaceThanMaxItemCountShouldFail() {
        assertThatThrownBy(() -> {
            Map.createMapFromLines(
                    Arrays.asList("nimi", "2", "x_o")
            );
        }).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("The map does not have enough floor tiles for max items.");
    }

    @Test
    public void creatingMapWithoutExitShouldFail() {
        assertThatThrownBy(() -> {
            Map.createMapFromLines(
                    Arrays.asList("nimi", "1", "x_")
            );
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Position of o not found");
    }
}
