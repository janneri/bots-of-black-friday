package fi.solita.botsofbf.dto;

public class ShootingLine {
    public final Position fromPosition;
    public final Position toPosition;
    public final int age;

    private ShootingLine(Position fromPosition, Position toPosition, int age) {
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.age = age;
    }

    public static ShootingLine of(Position fromPosition, Position toPosition) {
        return new ShootingLine(fromPosition, toPosition, 0);
    }

    public ShootingLine incAge() {
        return new ShootingLine(fromPosition, toPosition, age + 1);
    }
}
