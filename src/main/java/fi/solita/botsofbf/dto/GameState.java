package fi.solita.botsofbf.dto;

import java.util.*;

public class GameState {

    public final Set<Player> players = new HashSet<>();
    public int round = 1;

    public void addPlayer(final Player player) {
        boolean nameReserved = players.stream().anyMatch(p -> p.name.equals(player.name));
        if ( nameReserved ) {
            throw new IllegalArgumentException("Player already exists.");
        }

        this.players.add(player);
    }

    public void newRound() {
        round += 1;
    }

    public Player getPlayer(UUID playerId) {
        return players.stream().filter(p -> p.id.equals(playerId)).findFirst().get();
    }

    public Player movePlayer(UUID playerId, Move move) {
        // TODO tekisko mutablea vai ei?
        Player player = getPlayer(playerId);
        Player newPlayer = player.move(player.position.move(move));
        this.players.remove(player);
        this.players.add(newPlayer);
        return newPlayer;
    }
}
