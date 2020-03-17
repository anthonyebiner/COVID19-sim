import javafx.scene.shape.Circle;

import java.util.Set;

public class Person extends Circle {
    private Condition condition;
    private Vector2D vector;

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
        setFill(condition.color);
        vector = new Vector2D(vector.getNormalized().getMultiplied(condition.speed));
    }

    public Vector2D getDirVector() {
        return vector.get_xFlipped();
    }

    public void setDirVector(Vector2D vector) {
        this.vector = vector.get_xFlipped();
    }

    public Person(Condition condition, float x, float y, int angle) {
        super(Settings.RADIUS, condition.getColor());
        this.condition = condition;
        this.moveAbs(x, y);
        this.vector = new Vector2D(condition.getSpeed(), 0).getRotatedBy(Math.toRadians(angle)).get_xFlipped();
    }

    public double get_x() {
        return getCenterX();
    }

    public double get_y() {
        return Settings.HEIGHT - getCenterY();
    }

    public void moveAbs(double x, double y) {
        setCenterX(x);
        setCenterY(Settings.HEIGHT-y);
    }

    public void moveRel(double x, double y) {
        moveAbs(get_x() + x, get_y() + y);
    }

    public void advance() {
        condition.advance();
        moveRel(vector.x, -vector.y);
    }

    public boolean intersects(Person other) {
        return Math.sqrt(Math.pow(other.get_x() - this.get_x(), 2)
                + Math.pow(other.get_y() - this.get_y(), 2)) <= this.getRadius()*2;
    }

    public void infect(Person p) {
        condition.infect(p);
    }
}
