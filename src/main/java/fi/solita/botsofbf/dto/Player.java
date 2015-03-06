package fi.solita.botsofbf.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public class Player {
    @JsonIgnore
    public final UUID id;
    public final String name;
    public final String url;
    public final Position position;
    public final int movedOnTurn;

    // TODO rajoita DOSSAUSTA siten, että pelaaja voi tehdä vain rajallisen määrän liikkeitä?
    // Pelaajan tilaksi retired, jos/kun tulee mitta täyteen?
    public final int actionCount;

    private Player(UUID uuid, String name, String url, Position position, int actionCount, int movedOnTurn) {
        this.id = uuid;
        this.name = name;
        this.url = url;
        this.position = position;
        this.actionCount = actionCount;
        this.movedOnTurn = movedOnTurn;
    }

    public static Player create(String name, String url, Position position) {
        return new Player(UUID.randomUUID(), name, url, position, 0, 0);
    }

    public Player move(Position position, int turn) {
        if (turn > movedOnTurn) {
            return new Player(this.id, this.name, this.url, position, this.actionCount + 1, turn);
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
