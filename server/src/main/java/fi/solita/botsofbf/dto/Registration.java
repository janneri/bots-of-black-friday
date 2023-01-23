package fi.solita.botsofbf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Registration {
    public String playerName;

    public Registration(@JsonProperty("playerName") String playerName) {
        this.playerName = playerName;
    }
}
