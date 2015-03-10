package fi.solita.botsofbf.dto;

import java.util.*;
import java.util.stream.Collectors;

public class GameState {

    public final Map map;
    public final Set<Player> players;
    public final Set<Item> items;
    public final int round;

    public GameState(final Map map, final int round, final Set<Player> players, final Set<Item> items) {
        this.map = map;
        this.round = round;
        this.players = players;
        this.items = items;
    }

    public GameState() {
        this.map = Map.createDefault();
        this.round = 1;
        this.players = new HashSet<>();
        this.items = new HashSet<>();
    }

    public GameState addPlayer(final Player player) {
        boolean nameReserved = players.stream().anyMatch(p -> p.name.equals(player.name));
        if ( nameReserved ) {
            throw new IllegalArgumentException("Player already exists.");
        }

        Set<Player> newPlayers = new HashSet<>(players);
        newPlayers.add(player);
        return new GameState(map, round, newPlayers, items);
    }

    public GameState addItem(final Item item) {
        Set<Item> newItems = new HashSet<>(items);
        newItems.add(item);
        return new GameState(map, round, players, newItems);
    }

    public GameState newRound() {
        final Set<Player> alive = filterLivingPlayers(players);
        if (alive.size() < players.size()) {
            System.out.println(players.size() - alive.size() + " players died.");
        }
        return new GameState(map, round + 1, alive, items);
    }

    public Player getPlayer(UUID playerId) {
        return players.stream().filter(p -> p.id.equals(playerId)).findFirst().get();
    }

    public GameState movePlayer(UUID playerId, Move move) {
        final Player player = getPlayer(playerId);
        final Player newPlayer = move == Move.PICK ? player.pickItem(this) : player.move(player.position.move(move, map), round);

        final Set<Player> otherPlayers = players.stream().filter(p -> !p.id.equals(playerId)).collect(Collectors.toSet());
        otherPlayers.add(newPlayer);

        final Set<Item> newItems = removePicked(newPlayer); // Hivenen monimutkainen tapa noukkia.

        return new GameState(map, round, otherPlayers, newItems);
    }

    public GameState spawnItems() {
        if (Math.random() > 0.9) {
            return addItem(Item.create(100, map.randomPosition()));
        } else {
            return this;
        }
    }

    private boolean playerAlive(final Player p) {
        return p.movedOnRound == 0 || p.movedOnRound > round - 10;
    }

    private Set<Player> filterLivingPlayers(final Set<Player> players) {
        return players.stream().filter(this::playerAlive).collect(Collectors.toSet());
    }

    private Set<Item> removePicked(final Player picker) {
        if (picker.lastItem.isPresent()) {
            return items.stream().filter(i -> !i.equals(picker.lastItem.get())).collect(Collectors.toSet());
        } else {
            return items;
        }
    }
}
