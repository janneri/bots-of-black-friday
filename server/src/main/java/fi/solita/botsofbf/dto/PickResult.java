package fi.solita.botsofbf.dto;

public class PickResult {

    public final boolean itemWasPicked;
    public final Player newPlayer;

    public PickResult(boolean itemWasPicked, Player newPlayer) {
        this.itemWasPicked = itemWasPicked;
        this.newPlayer = newPlayer;
    }
}
