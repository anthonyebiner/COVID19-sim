import javafx.scene.paint.Color;

public class Deceased extends Condition {
    private Person person;

    public Deceased(Person person) {
        this.dead = true;
        this.speed = 0.1;
        this.color = Color.BLACK;
    }
}
