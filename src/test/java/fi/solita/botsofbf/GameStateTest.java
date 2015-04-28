package fi.solita.botsofbf;


import fi.solita.botsofbf.dto.GameState;
import fi.solita.botsofbf.dto.Map;
import fi.solita.botsofbf.dto.Player;
import fi.solita.botsofbf.dto.Position;
import org.junit.Test;

import java.util.stream.Stream;

public class GameStateTest {
    
    @Test
    public void testMove() {
        GameState state = new GameState(Map.createDefault());
        state = addPlayers(state, 5);


    }

    private GameState addPlayers(GameState currentState, int count) {
        for (int i = 0; i < count; i++) {
            Position randomValidPosition = currentState.map.randomValidPosition(Stream.<Position>empty());
            currentState = currentState.addPlayer(Player.create("p" + i, "url" + i, randomValidPosition));
        }
        return currentState;
    }


}