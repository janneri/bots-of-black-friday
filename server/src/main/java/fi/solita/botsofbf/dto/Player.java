package fi.solita.botsofbf.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;
import java.util.stream.Collectors;

public class Player {

    public static final int INITIAL_MONEY_LEFT = 5000;
    public static final int INITIAL_HEALTH_LEFT = 100;

    @JsonIgnore
    public final UUID id;
    public final String name;
    public final String url;
    public final Position position;
    public final int score;
    public final int money;
    public final PlayerState state;
    public final int timeInState;
    @JsonIgnore
    public final Optional<Item> lastItem;
    public final List<Item> usableItems;
    public final int actionCount;
    public final int health; // 0-100%


    private Player(UUID uuid, String name, String url, Position position, int actionCount,
                   int score, int money, PlayerState state, int timeInState, Optional<Item> lastItem,
                   int health, List<Item> usableItems) {
        this.id = uuid;
        this.name = name;
        this.url = url;
        this.position = position;
        this.actionCount = actionCount;
        this.score = score;
        this.money = money;
        this.state = state;
        this.timeInState = timeInState;
        this.lastItem = lastItem;
        this.health = health;
        this.usableItems = usableItems;
    }

    public static Player create(String name, String url, Position position) {
        final int score = 0;
        final int actionCount = 0;
        final int timeInState = 0;
        return new Player(UUID.randomUUID(), name, url, position, actionCount, score,
                INITIAL_MONEY_LEFT, PlayerState.MOVE, timeInState, Optional.<Item>empty(),
                INITIAL_HEALTH_LEFT, Collections.<Item>emptyList());
    }

    public Player move(Position position) {
        final int timeInState = 0;
        return new Player(this.id, this.name, this.url, position, this.actionCount + 1,
                this.score, this.money, PlayerState.MOVE, timeInState, Optional.<Item>empty(),
                this.health, this.usableItems);
    }

    public Player decreaseHealth(int amount) {
        return new Player(this.id, this.name, this.url, position, this.actionCount,
                this.score, this.money, this.state, this.timeInState, Optional.<Item>empty(),
                this.health - amount, this.usableItems);
    }

    public Player cancelState() {
        return new Player(this.id, this.name, this.url, position, this.actionCount + 1,
                this.score, this.money, PlayerState.MOVE, 0, Optional.<Item>empty(),
                this.health, this.usableItems);
    }

    public boolean hasUnusedWeapon() {
        return usableItems.stream()
                .anyMatch(item -> item.isUsable && item.type == Item.Type.WEAPON);
    }

    public Player useFirstUsableItem() {
        List<Item> newItems = this.usableItems.stream().skip(1).collect(Collectors.toList());

        return new Player(this.id, this.name, this.url, this.position, this.actionCount + 1,
                this.score, this.money, PlayerState.MOVE, this.timeInState, this.lastItem,
                this.health, newItems);
    }

    public PickResult pickItem(Item item) {
        if (this.money < item.discountedPrice) {
            throw new IllegalStateException("No money left");
        }

        if (timeInState + 1 >= item.getPickTime()) {
            ArrayList<Item> newItems = new ArrayList<>(this.usableItems);
            if ( item.isUsable && item.type == Item.Type.WEAPON ) {
                newItems.add(item);
            }

            Player player = new Player(this.id, this.name, this.url, this.position, this.actionCount + 1,
                    this.score + item.price, this.money - item.discountedPrice, PlayerState.MOVE, 0, Optional.of(item),
                    this.health, newItems);

            return new PickResult(true, player);
        } else {
            Player player = new Player(this.id, this.name, this.url, this.position, this.actionCount + 1,
                    this.score, this.money, PlayerState.PICK, this.timeInState + 1, Optional.of(item),
                    this.health, this.usableItems);

            return new PickResult(false, player);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (id != null ? !id.equals(player.id) : player.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
