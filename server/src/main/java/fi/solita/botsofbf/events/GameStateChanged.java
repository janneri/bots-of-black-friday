package fi.solita.botsofbf.events;

import fi.solita.botsofbf.dto.GameState;
import fi.solita.botsofbf.dto.Player;

import java.util.UUID;

public class GameStateChanged {
    public final String reason;
    public final GameState gameState;
    public final Player playerState;
    public final UUID playerId;

    private GameStateChanged(String reason, GameState gameState, Player playerState) {
        this.reason = reason;
        this.gameState = gameState;
        this.playerState = playerState;
        this.playerId = playerState == null ? null : playerState.id;
    }

    public static GameStateChanged create(String reason, GameState gameState, Player playerState) {
        return new GameStateChanged(reason, gameState, playerState);
    }
}
