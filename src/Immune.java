import javafx.scene.paint.Color;

public class Immune extends Condition {
    private Person person;

    public Immune(Person person) {
        this.dead = false;
        this.speed = 2;
        this.color = Color.LIGHTBLUE;
    }

    public Immune(Person person, double speed) {
        this.dead = false;
        this.speed = speed;
        this.color = Color.LIGHTBLUE;
    }
}
