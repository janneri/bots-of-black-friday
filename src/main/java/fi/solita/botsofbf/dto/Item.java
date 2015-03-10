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

}
