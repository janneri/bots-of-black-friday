package fi.solita.botsofbf.dto;

import java.util.*;
import java.util.stream.Collectors;

public class GameState {

    public final Map map;
    public final Set<Player> players;
    public final Set<Player> finishedPlayers;
    public final Set<Item> items;
    public final int round;

    public GameState(final Map map) {
        this.map = map;
        this.round = 1;
        this.finishedPlayers = new HashSet<>();
        this.players = new HashSet<>();
        this.items = new HashSet<>();
    }

    private GameState(final Map map, final int round, final Set<Player> players, Set<Player> finishedPlayers, final Set<Item> items) {
        this.map = map;
        this.round = round;
        this.players = players;
        this.finishedPlayers = finishedPlayers;
        this.items = items;
    }

    public GameState addPlayer(final Player player) {
        boolean nameReserved = players.stream().anyMatch(p -> p.name.equals(player.name));
        if ( nameReserved ) {
            throw new IllegalArgumentException("Player already exists.");
        }

        Set<Player> newPlayers = new HashSet<>(players);
        newPlayers.add(player);
        return new GameState(map, round, newPlayers, finishedPlayers, items);
    }

    public GameState addItem(final Item item) {
        Set<Item> newItems = new HashSet<>(items);
        newItems.add(item);
        return new GameState(map, round, players, finishedPlayers, newItems);
    }

    public GameState newRound() {
        return new GameState(map, round + 1, players, finishedPlayers, items);
    }

    public Player getPlayer(UUID playerId) {
        return players.stream().filter(p -> p.id.equals(playerId)).findFirst().get();
    }

    public GameState addInvalidMove(Player player) {
        Player newPlayer = player.incInvalidActions();
        return new GameState(map, round, replacePlayer(players, newPlayer), finishedPlayers, items);
    }

    public GameState removeDeadPlayers() {
        Set<Player> alivePlayers = players.stream()
                .filter(p -> p.health > 0)
                .collect(Collectors.toSet());

        return new GameState(map, round, alivePlayers, finishedPlayers, items);
    }


    private Set<Player> getOtherPlayers(Player removedPlayer) {
        return players.stream().filter(p -> !p.id.equals(removedPlayer.id)).collect(Collectors.toSet());
    }

    private boolean playerGotItem(Move move, Player player) {
        return move == Move.PICK && player.lastItem.isPresent() && player.timeInState >= player.lastItem.get().getPickTime();
    }

    public GameState movePlayer(UUID playerId, Move move) {
        final Player player = getPlayer(playerId);
        final Player newPlayer = move == Move.PICK ? pickItem(player) : movePlayer(player, move);
        final Set<Player> newPlayers = replacePlayer(players, newPlayer);
        final Set<Item> newItems = playerGotItem(move, player) ? removeItem(player.position) : items;

        if ( map.exit.equals(newPlayer.position) ) {
            finishedPlayers.add(newPlayer);
            return new GameState(map, round, getOtherPlayers(newPlayer), finishedPlayers, newItems);
        }
        else {
            return new GameState(map, round, newPlayers, finishedPlayers, newItems);
        }
    }

    public Set<Item> removeItem(Position position) {
        return items.stream().filter(i -> !i.position.equals(position)).collect(Collectors.toSet());
    }

    private Player movePlayer(Player player, Move move) {
        if ( map.isValidPosition(player.position.move(move, map.width, map.height)) ) {
            return player.move(player.position.move(move, map.width, map.height));
        }
        else {
            throw new IllegalStateException(String.format("Invalid move from", player.name));
        }
    }

    private Player pickItem(Player player) {
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
        if (items.size() < map.maxItemCount && Math.random() > 0.9) {
            int price = randomBetween(100, Player.INITIAL_MONEY_LEFT);
            int discountPercent = randomBetween(10, 90);
            Position pos = map.randomValidPosition(items.stream().map(i -> i.position));
            return addItem(Item.create(price, discountPercent, pos));
        } else {
            return this;
        }
    }

    private static int randomBetween(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

}