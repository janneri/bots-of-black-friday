package fi.solita.botsofbf.dto;

import java.util.UUID;

public final class RegisterResponse {
    public final UUID id;
    public final Player player;
    public final Map map;

    public RegisterResponse(Player player, Map map) {
        this.id = player.id;
        this.player = player;
        this.map = map;
    }
}
