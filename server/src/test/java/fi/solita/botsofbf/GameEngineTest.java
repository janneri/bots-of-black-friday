package fi.solita.botsofbf;


import fi.solita.botsofbf.dto.GameState;
import fi.solita.botsofbf.dto.Map;
import fi.solita.botsofbf.dto.Move;
import fi.solita.botsofbf.dto.Player;
import fi.solita.botsofbf.dto.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameEngineTest {

    @Mock
    private UiClient uiClient;

    @BeforeEach
    public void initMocks(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void healthDecreasesBecauseOfInvalidMoves() {
        Player player = createPlayerToPos(1, 0);
        GameState state = new GameState(Map.createMapFromLines(Arrays.asList("nimi", "5", "x_o")))
                .addPlayer(player);

        GameEngine gameEngine = new GameEngine(state, uiClient);
        gameEngine.registerMove(player.id, Move.LEFT);
        gameEngine.tick();

        assertEquals(player.health - GameState.HEALTH_LOST_WHEN_INVALID_MOVE,
                gameEngine.getCurrentState().getPlayer(player.id).health);
    }

    @Test
    public void deadPlayersAreRemoved() {
        Player player = createPlayerToPos(1, 0);
        GameState state = new GameState(Map.createMapFromLines(Arrays.asList("nimi", "5", "x_o")))
                .addPlayer(createPlayerToPos(1, 0))
                .addPlayer(player.decreaseHealth(99))
                .addPlayer(createPlayerToPos(1, 0));

        GameEngine gameEngine = new GameEngine(state, uiClient);

        assertEquals(3, gameEngine.getCurrentState().players.size());
        gameEngine.tick();
        assertEquals(2, gameEngine.getCurrentState().players.size());
    }


    private Player createPlayerToPos(int x, int y) {
        return Player.create(UUID.randomUUID().toString(), Position.of(x, y));
    }

}