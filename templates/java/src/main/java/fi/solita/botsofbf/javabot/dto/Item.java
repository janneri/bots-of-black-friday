package fi.solita.botsofbf.javabot.dto;

public record Item(int price, int discountPercent, Position position, boolean isUsable) {
}
