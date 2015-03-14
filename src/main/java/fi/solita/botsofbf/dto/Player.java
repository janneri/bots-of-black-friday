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
    public final int score;
    public final int money;
    public static final int INITIAL_MONEY_LEFT = 5000;

    @JsonIgnore
    public final Optional<Item> lastItem;

    // TODO rajoita DOSSAUSTA siten, että pelaaja voi tehdä vain rajallisen määrän liikkeitä?
    // Pelaajan tilaksi retired, jos/kun tulee mitta täyteen?
    public final int actionCount;

    @JsonIgnore
    public final int invalidActionCount;

    private Player(UUID uuid, String name, String url, Position position, int actionCount,
                   int score, int money, Optional<Item> lastItem, int invalidActionCount) {
        this.id = uuid;
        this.name = name;
        this.url = url;
        this.position = position;
        this.actionCount = actionCount;
        this.score = score;
        this.money = money;
        this.lastItem = lastItem;
        this.invalidActionCount = invalidActionCount;
    }

    public static Player create(String name, String url, Position position) {
        int invalidActionCount = 0;
        int score = 0;

        return new Player(UUID.randomUUID(), name, url, position, 0, score,
                INITIAL_MONEY_LEFT, Optional.<Item>empty(), invalidActionCount);
    }

    public Player move(Position position) {
        return new Player(this.id, this.name, this.url, position, this.actionCount + 1,
                this.score, this.money, Optional.<Item>empty(), this.invalidActionCount);
    }

    public Player incInvalidActions() {
        return new Player(this.id, this.name, this.url, position, this.actionCount + 1,
                this.score, this.money, Optional.<Item>empty(), this.invalidActionCount + 1);
    }

    public Player pickItem(Item item) {
        if (this.money < item.discountedPrice) {
            throw new IllegalStateException("No money left");
        }

        return new Player(this.id, this.name, this.url, this.position, this.actionCount + 1,
                this.score + item.price, this.money - item.discountedPrice, Optional.of(item),
                this.invalidActionCount);
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
