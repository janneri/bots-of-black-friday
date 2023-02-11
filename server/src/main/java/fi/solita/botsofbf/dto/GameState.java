package fi.solita.botsofbf.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.solita.botsofbf.exception.InvalidPlayerNameException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class GameState {

    public static final int HEALTH_LOST_WHEN_ATTACKED = 50;
    public static final int HEALTH_GAINED_WHEN_ATTACKING = 50;
    /** Every N moves causes health to decrease. */
    public static final int AGING_MOVE_COUNT = 50;
    public static final int HEALTH_LOST_WHEN_AGED = 10;
    public static final int HEALTH_LOST_WHEN_TRAPPED = 10;
    public static final int HEALTH_LOST_WHEN_INVALID_MOVE = 20;

    @JsonIgnore
    public final Map map;
    public final Set<Player> players;
    public final List<Player> finishedPlayers;
    public final Set<Item> items;
    public final int round;
    public final List<ShootingLine> shootingLines;

    public GameState(final Map map) {
        this.map = map;
        this.round = 1;
        this.finishedPlayers = new ArrayList<>();
        this.players = new HashSet<>();
        this.items = new HashSet<>();
        this.shootingLines = new ArrayList<>();
    }

    private GameState(final Map map, final int round, final Set<Player> players,
                      List<Player> finishedPlayers, final Set<Item> items, final List<ShootingLine> shootingLines) {
        this.map = map;
        this.round = round;
        this.players = players;
        this.finishedPlayers = finishedPlayers;
        this.items = items;
        this.shootingLines = shootingLines;
    }

    public void throwIfNameInvalid(final String playerName) {
        boolean nameReserved = players.stream().anyMatch(p -> p.name.equals(playerName));
        int nameLenght = playerName == null ? 0 : playerName.length();
        if (nameLenght < 1 || nameLenght > 20) {
            throw new InvalidPlayerNameException("Registered name " + playerName + " length " + nameLenght + " is invalid");
        }
        if ( nameReserved ) {
            throw new InvalidPlayerNameException("Player already exists.");
        }
    }

    public GameState addPlayer(final Player player) {
        boolean nameReserved = players.stream().anyMatch(p -> p.name.equals(player.name));
        Set<Player> newPlayers = new HashSet<>(players);

        if (nameReserved) {
            System.out.println(player.name + "already exists");
        }
        else {
            newPlayers.add(player);
        }

        return new GameState(map, round, newPlayers, finishedPlayers, items, shootingLines);
    }

    public GameState addItem(final Item item) {
        if (!map.isMovablePosition(item.position)) {
            throw new IllegalArgumentException(
                    String.format("Item position %s is not movable.", item.position)
            );
        }
        boolean conflictingPosition = items.stream()
                .anyMatch(oldItem -> oldItem.position.equals(item.position));
        if (conflictingPosition) {
            throw new IllegalArgumentException(
                    String.format("Item position %s already taken.", item.position)
            );
        }
        Set<Item> newItems = new HashSet<>(items);
        newItems.add(item);
        return new GameState(map, round, players, finishedPlayers, newItems, shootingLines);
    }

    public GameState newRound() {
        List<ShootingLine> newShootingLines = shootingLines.stream()
                .map(ShootingLine::incAge)
                .filter(line -> line.age < 3)
                .collect(Collectors.toList());
        return new GameState(map, round + 1, players, finishedPlayers, items, newShootingLines);
    }

    public Player getPlayer(UUID playerId) {
        return players.stream().filter(p -> p.id.equals(playerId)).findFirst().get();
    }

    public GameState addInvalidMove(Player player) {
        Player newPlayer = player.decreaseHealth(HEALTH_LOST_WHEN_INVALID_MOVE).cancelState();
        return new GameState(map, round, replacePlayer(players, newPlayer), finishedPlayers, items, shootingLines);
    }

    public GameState removeDeadPlayers() {
        Set<Player> alivePlayers = players.stream()
                .filter(p -> p.health > 0)
                .collect(Collectors.toSet());

        String deadPlayerNames = players.stream()
                .dropWhile(alivePlayers::contains)
                .map(p -> p.name)
                .collect(Collectors.joining(","));

        if (!deadPlayerNames.isEmpty()) {
            System.out.println("Removed dead players: " + deadPlayerNames);
        }
        return new GameState(map, round, alivePlayers, finishedPlayers, items, shootingLines);
    }


    private Set<Player> getOtherPlayers(Player removedPlayer) {
        return players.stream().filter(p -> !p.id.equals(removedPlayer.id)).collect(Collectors.toSet());
    }

    private static int manhattanDistance(Player p1, Player p2) {
        return Math.abs(p1.position.x - p2.position.x) + Math.abs(p1.position.y - p2.position.y);
    }

    private static Optional<Player> findFurthestPlayer(Player fromPlayer, Set<Player> players) {
        return players.stream()
                .filter(p -> !p.id.equals(fromPlayer.id))
                .sorted((p1, p2) -> manhattanDistance(fromPlayer, p2) - manhattanDistance(fromPlayer, p1))
                .findFirst();
    }

    public GameState movePlayer(UUID playerId, Move move) {
        final Player player = getPlayer(playerId);
        Player newPlayer = null;
        Optional<Player> affectedPlayer = Optional.empty();
        final List<ShootingLine> newShootingLines = new ArrayList(shootingLines);
        boolean itemWasPicked = false;

        if (move == null) {
            throw new IllegalStateException(String.format("%s has not registered a move", player.name));
        }

        if (move == Move.PICK) {
            final Optional<Item> item = items.stream().filter(i -> i.position.equals(player.position)).findFirst();
            PickResult pickResult = player.pickItem(item.orElseThrow(() -> new IllegalStateException(
                    String.format("%s is trying to pick from invalid location", player.name))));

            newPlayer = pickResult.newPlayer;
            itemWasPicked = pickResult.itemWasPicked;
        }
        else if ( move == Move.USE ) {
            if ( !player.hasUnusedWeapon() ) {
                throw new IllegalStateException(String.format("%s does not have a usable item", player.name));
            }
            Optional<Player> furthestPlayer = findFurthestPlayer(player, players);
            if ( furthestPlayer.isPresent() ) {
                affectedPlayer = Optional.of(furthestPlayer.get().decreaseHealth(HEALTH_LOST_WHEN_ATTACKED));
                newPlayer = player.useFirstUsableItem();
                newPlayer = newPlayer.increaseHealth(HEALTH_GAINED_WHEN_ATTACKING);
                newShootingLines.add(ShootingLine.of(player.position, furthestPlayer.get().position));
            }
        }
        else {
            newPlayer = movePlayer(player, move);
        }

        if (newPlayer.actionCount % AGING_MOVE_COUNT == 0 ) {
            newPlayer = newPlayer.decreaseHealth(HEALTH_LOST_WHEN_AGED);
        }


        final Set<Player> newPlayers = affectedPlayer.isPresent() ?
                replacePlayer(replacePlayer(players, newPlayer), affectedPlayer.get()) :
                replacePlayer(players, newPlayer);

        final Set<Item> newItems = itemWasPicked ? removeItem(newPlayer.position) : items;

        if ( map.exit.equals(newPlayer.position) ) {
            final String pName = newPlayer.name;
            Optional<Player> existing = finishedPlayers.stream().filter(p -> p.name.equals(pName)).findAny();
            Player finishedPlayer = newPlayer;

            if ( existing.isPresent() ) {
                finishedPlayer = Arrays.asList(existing.get(), newPlayer).stream()
                        .sorted((p1, p2) -> p2.score - p1.score)
                        .findFirst()
                        .get();
            }

            List<Player> newFinishedPlayers = finishedPlayers.stream()
                    .filter(p -> !p.name.equals(pName)).collect(Collectors.toList());

            newFinishedPlayers.add(finishedPlayer);

            return new GameState(map, round, getOtherPlayers(newPlayer), newFinishedPlayers, newItems, newShootingLines);
        }
        else {
            return new GameState(map, round, newPlayers, finishedPlayers, newItems, newShootingLines);
        }
    }

    public Set<Item> removeItem(Position position) {
        return items.stream().filter(i -> !i.position.equals(position)).collect(Collectors.toSet());
    }

    private Player movePlayer(Player player, Move move) {
        final Position newPos = player.position.move(move, map.width, map.height);
        if (map.isMovablePosition(newPos) ) {
            final Player newPlayer = player.move(newPos);
            if (map.isTrap(newPos)) {
                return newPlayer.decreaseHealth(HEALTH_LOST_WHEN_TRAPPED);
            }
            return newPlayer;
        }
        else {
            throw new IllegalStateException(String.format("Invalid move from %s to unmovable pos %s", player.position, newPos));
        }
    }

    private Set<Player> replacePlayer(Set<Player> players, Player newPlayer) {
        final Set<Player> otherPlayers = players.stream()
                .filter(p -> !p.id.equals(newPlayer.id)).collect(Collectors.toSet());
        otherPlayers.add(newPlayer);
        return otherPlayers;
    }

    private Position randomFreeFloorPosition() {
        final Set<Position> takenPositions = items.stream()
                .map(item -> item.position).collect(Collectors.toSet());
        return map.randomFloorPosition(takenPositions);
    }

    public GameState spawnItems() {
        final Position randomFloorPosition = randomFreeFloorPosition();
        if (randomFloorPosition == null) {
            System.out.println("Could not find free floor space for new items.");
            return this;
        }

        if (items.size() < map.maxItemCount && Math.random() < 0.1) {
            return addItem(Item.createPotion(randomFloorPosition));
        }
        if (items.size() < map.maxItemCount && Math.random() > 0.9) {
            int price = randomBetween(100, Player.INITIAL_MONEY_LEFT);
            int discountPercent = randomBetween(10, 90);
            Item newItem = discountPercent > 70 ?
                    Item.createWeapon(price, discountPercent, randomFloorPosition) :
                    Item.create(price, discountPercent, randomFloorPosition);
            return addItem(newItem);
        } else {
            return this;
        }
    }

    private static Random random;
    private static int randomBetween(int min, int max) {
        try {
            if (random == null) {
                random = SecureRandom.getInstanceStrong();
            }
            return random.nextInt(max - min + 1) + min;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
