import javafx.scene.paint.Color;

public abstract class Condition {
    boolean dead;
    double speed;
    int incubation;
    Color color;

    public boolean isDead() {
        return dead;
    }

    public double getSpeed() {
        return speed;
    }

    public int getIncubation() {
        return incubation;
    }

    public Color getColor() {
        return color;
    }

    public void infect(Person p1) {}

    public void advance() {}

    public boolean equals(Object obj) {
        return this == obj;
    }
}
