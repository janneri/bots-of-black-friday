package fi.solita.botsofbf.events;

import fi.solita.botsofbf.dto.GameState;
import fi.solita.botsofbf.dto.Map;

public class GameStateChanged {
    public final String reason;
    public final GameState gameState;
    public final Map newMap;

    private GameStateChanged(String reason, GameState gameState, Map newMap) {
        this.reason = reason;
        this.gameState = gameState;
        this.newMap = newMap;
    }

    public static GameStateChanged create(String reason, GameState gameState, Map newMap) {
        return new GameStateChanged(reason, gameState, newMap);
    }
}
