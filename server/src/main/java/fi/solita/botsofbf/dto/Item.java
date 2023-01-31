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
        WEAPON,
        POTION
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

    public static Item createPotion(int price, int discountPercent, Position position) {
        final boolean isUsable = false;
        return new Item(price, discountPercent, position, Type.POTION, isUsable);
    }

    @JsonIgnore
    public int getPickTime() {
        return Math.round(discountPercent / 10);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (discountPercent != item.discountPercent) return false;
        if (isUsable != item.isUsable) return false;
        if (price != item.price) return false;
        if (position != null ? !position.equals(item.position) : item.position != null) return false;
        if (type != item.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = price;
        result = 31 * result + discountPercent;
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (isUsable ? 1 : 0);
        return result;
    }

}
