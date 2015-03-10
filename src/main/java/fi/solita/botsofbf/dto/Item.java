package fi.solita.botsofbf.dto;

public class Item {

    public final int price;
    public final Position position;

    private Item(int price, Position position) {
        this.price = price;
        this.position = position;
    }

    public static Item create(int price, Position position) {
        return new Item(price,  position);
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
}
