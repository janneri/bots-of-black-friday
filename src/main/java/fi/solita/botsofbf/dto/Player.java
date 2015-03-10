package fi.solita.botsofbf.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public class Player {
    @JsonIgnore
    public final UUID id;
    public final String name;
    public final String url;
    public final Position position;
    public final int movedOnRound;

    // TODO rajoita DOSSAUSTA siten, että pelaaja voi tehdä vain rajallisen määrän liikkeitä?
    // Pelaajan tilaksi retired, jos/kun tulee mitta täyteen?
    public final int actionCount;

    private Player(UUID uuid, String name, String url, Position position, int actionCount, int movedOnRound) {
        this.id = uuid;
        this.name = name;
        this.url = url;
        this.position = position;
        this.actionCount = actionCount;
        this.movedOnRound = movedOnRound;
    }

    public static Player create(String name, String url, Position position, int startRound) {
        return new Player(UUID.randomUUID(), name, url, position, 0, startRound);
    }

    public Player move(Position position, int round) {
        if (round > movedOnRound) {
            return new Player(this.id, this.name, this.url, position, this.actionCount + 1, round);
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
