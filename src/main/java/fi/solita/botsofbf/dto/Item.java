package fi.solita.botsofbf.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Item {

    public final int price;
    public final int discountPercent;
    public final Position position;
    @JsonIgnore
    public final int discount;
    @JsonIgnore
    public final int discountedPrice;
    public final Type type;
    public final boolean isUsable;

    public enum Type {
        JUST_SOME_JUNK,
        WEAPON
    }

    private Item(int price, int discountPercent, Position position, Type type, boolean isUsable) {
        this.price = price;
        this.discountPercent = discountPercent;
        this.discount = (int)(price * ((double)discountPercent/100));
        this.discountedPrice = this.price - this.discount;
        this.position = position;
        this.type = type;
        this.isUsable = isUsable;
    }

    public static Item create(int price, int discountPercent, Position position) {
        final boolean isUsable = false;
        return new Item(price, discountPercent, position, Type.JUST_SOME_JUNK, isUsable);
    }

    public static Item createWeapon(int price, int discountPercent, Position position) {
        final boolean isUsable = true;
        return new Item(price, discountPercent, position, Type.WEAPON, isUsable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (price != item.price) return false;
        if (position != null ? !position.equals(item.position) : item.position != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = price;
        result = 31 * result + (position != null ? position.hashCode() : 0);
        return result;
    }

    @JsonIgnore
    public int getPickTime() {
        return Math.round(discountPercent / 10);
    }
}
