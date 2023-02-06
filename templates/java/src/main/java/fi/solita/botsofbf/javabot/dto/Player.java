package fi.solita.botsofbf.javabot.dto;

import java.util.List;

public record Player(String name, int health, Position position, int money, List<Item> usableItems) { }
