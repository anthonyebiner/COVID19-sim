import javafx.scene.paint.Color;

public class Healthy extends Condition {
    public Healthy() {
        this.dead = false;
        this.speed = 2;
        this.incubation = 0;
        this.color = Color.LIGHTGREEN;
    }

    public Healthy(double speed) {
        this();
        this.speed = speed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }
}
