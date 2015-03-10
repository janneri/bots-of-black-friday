package fi.solita.botsofbf.events;

import fi.solita.botsofbf.dto.GameState;
import fi.solita.botsofbf.dto.Player;

public class GameStateChanged {
    public final String reason;
    public final GameState gameState;
    public final Player playerState;

    private GameStateChanged(String reason, GameState gameState, Player playerState) {
        this.reason = reason;
        this.gameState = gameState;
        this.playerState = playerState;
    }

    public static GameStateChanged create(String reason, GameState gameState, Player playerState) {
        return new GameStateChanged(reason, gameState, playerState);
    }
}
