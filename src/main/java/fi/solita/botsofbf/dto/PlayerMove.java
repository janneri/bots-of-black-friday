package fi.solita.botsofbf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerMove {
    public final String id;
    public final Move move;

    public PlayerMove(@JsonProperty("id") String id, @JsonProperty("move") Move move) {
        this.id = id;
        this.move = move;
    }
}
