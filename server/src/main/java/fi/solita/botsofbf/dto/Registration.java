package fi.solita.botsofbf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Registration {
    public String playerName;
    public String url;

    public Registration(@JsonProperty("playerName") String playerName, @JsonProperty("url") String url) {
        this.playerName = playerName;
        this.url = url;
    }
}
