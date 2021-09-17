package fi.solita.botsofbf.dto;

public class Item {
    public int price;
    public Position position;
    public boolean isUsable;

    public enum Type {
        JUST_SOME_JUNK,
        WEAPON
    }
}
