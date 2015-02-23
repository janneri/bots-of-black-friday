package fi.solita.botsofbf.dto;

import java.util.UUID;

public final class RegisterResponse {
    public final UUID id;
    public final Player player;
    public final GameState gameState;

    public RegisterResponse(Player player, GameState gameState) {
        this.id = player.id;
        this.player = player;
        this.gameState = gameState;
    }
}
