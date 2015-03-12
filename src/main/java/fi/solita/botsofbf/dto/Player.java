package fi.solita.botsofbf.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Optional;
import java.util.UUID;

public class Player {
    @JsonIgnore
    public final UUID id;
    public final String name;
    public final String url;
    public final Position position;
    public final int movedOnRound;
    public final int score;
    public final int money;

    @JsonIgnore
    public final Optional<Item> lastItem;

    // TODO rajoita DOSSAUSTA siten, että pelaaja voi tehdä vain rajallisen määrän liikkeitä?
    // Pelaajan tilaksi retired, jos/kun tulee mitta täyteen?
    public final int actionCount;

    private Player(UUID uuid, String name, String url, Position position, int actionCount, int movedOnRound, int score, int money, Optional<Item> lastItem) {
        this.id = uuid;
        this.name = name;
        this.url = url;
        this.position = position;
        this.actionCount = actionCount;
        this.movedOnRound = movedOnRound;
        this.score = score;
        this.money = money;
        this.lastItem = lastItem;
    }

    public static Player create(String name, String url, Position position, int startRound) {
        return new Player(UUID.randomUUID(), name, url, position, 0, startRound, 0, 5000, Optional.<Item>empty());
    }

    public Player move(Position position, int round) {
        if (round > movedOnRound) {
            return new Player(this.id, this.name, this.url, position, this.actionCount + 1, round, this.score, money, Optional.<Item>empty());
        }
        return this;
    }

    public Player pickItem(GameState state) {
        final Optional<Item> item = state.items.stream().filter(i -> i.position.equals(position)).findFirst();

        if (item.isPresent() && money < item.get().price) {
            throw new IllegalStateException("No money left");
        }

        if (state.round > movedOnRound && item.isPresent() ) {
            return new Player(this.id, this.name, this.url, position, this.actionCount + 1, state.round,
                    this.score + item.get().price, money - item.get().price, item);
        }
        return this;
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
