package fi.solita.botsofbf.dto;

import java.util.*;
import java.util.stream.Collectors;

public class GameState {

    public final Map map;
    public final Set<Player> players;
    public final Set<Item> items;
    public final int round;
    public static final int MAX_INVALID_ACTIONS = 5;

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
        return new GameState(map, round + 1, players, items);
    }

    public Player getPlayer(UUID playerId) {
        return players.stream().filter(p -> p.id.equals(playerId)).findFirst().get();
    }

    public GameState addInvalidMove(Player player) {
        Player newPlayer = player.incInvalidActions();
        if ( newPlayer.invalidActionCount > MAX_INVALID_ACTIONS ) {
            return new GameState(map, round, getOtherPlayers(newPlayer), items);
        }
        else {
            return new GameState(map, round, replacePlayer(players, newPlayer), items);
        }
    }

    private Set<Player> getOtherPlayers(Player removedPlayer) {
        return players.stream().filter(p -> !p.id.equals(removedPlayer.id)).collect(Collectors.toSet());
    }


    public GameState movePlayer(UUID playerId, Move move) {
        final Player player = getPlayer(playerId);
        final Player newPlayer = move == Move.PICK ? pickItem(player) : player.move(player.position.move(move, map));
        final Set<Player> newPlayers = replacePlayer(players, newPlayer);
        final Set<Item> newItems = move == Move.PICK ? removeItem(player.position) : items;

        return new GameState(map, round, newPlayers, newItems);
    }

    public Set<Item> removeItem(Position position) {
        return items.stream().filter(i -> !i.position.equals(position)).collect(Collectors.toSet());
    }

    public Player pickItem(Player player) {
        final Optional<Item> item = items.stream().filter(i -> i.position.equals(player.position)).findFirst();

        if ( !item.isPresent() ) {
            throw new IllegalStateException(String.format("%s is trying to pick from invalid location", player.name));
        }

        return player.pickItem(item.get());
    }

    private Set<Player> replacePlayer(Set<Player> players, Player newPlayer) {
        final Set<Player> otherPlayers = players.stream()
                .filter(p -> !p.id.equals(newPlayer.id)).collect(Collectors.toSet());
        otherPlayers.add(newPlayer);
        return otherPlayers;
    }

    public GameState spawnItems() {
        if (Math.random() > 0.9) {
            int price = randomBetween(100, Player.INITIAL_MONEY_LEFT);
            int discountPercent = randomBetween(10, 90);
            return addItem(Item.create(price, discountPercent, map.randomPosition()));
        } else {
            return this;
        }
    }

    private static int randomBetween(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

}