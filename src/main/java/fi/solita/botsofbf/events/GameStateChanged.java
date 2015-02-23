package fi.solita.botsofbf.events;

import fi.solita.botsofbf.dto.GameState;

public class GameStateChanged {
    public final String reason;
    public final GameState gameState;

    private GameStateChanged(String reason, GameState gameState) {
        this.reason = reason;
        this.gameState = gameState;
    }

    public static GameStateChanged create(String reason, GameState gameState) {
        return new GameStateChanged(reason, gameState);
    }
}
