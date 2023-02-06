package fi.solita.botsofbf.javabot.dto;

import java.util.List;

public record GameState(List<Item> items, List<Player> players) { }
