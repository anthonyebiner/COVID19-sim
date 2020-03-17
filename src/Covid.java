import javafx.scene.paint.Color;

import java.util.Random;

public class Covid extends Condition {
    private int time;
    private Person person;
    private double mortality;
    private Random rand = new Random();
    private int stage = 0;

    public Covid(Person person) {
        this.dead = false;
        this.speed = 2;
        this.incubation = 0;
        this.color = Color.LIGHTSALMON;
        this.person = person;
        mortality = 0.03;

    }

    public Covid(double speed, Person p) {
        this(p);
        this.speed = speed;
    }

    public void infect(Person p) {
        if (p.getCondition().getClass() != getClass() && stage != 2)
            p.setCondition(new Covid(p.getCondition().speed, p));
    }

    public void advance() {
        time += 1;
        if (time > Settings.FRAMERATE * 3 && stage == 0) {
            this.color = Color.DARKRED;
            this.speed = 0.1;
            stage = 1;
            person.setCondition(this);
        } else if (time > Settings.FRAMERATE * 6 && stage == 1) {
            if (rand.nextDouble() < mortality) {
                this.color = Color.BLACK;
                this.speed = 0;
            } else {
                this.color = Color.LIGHTBLUE;
                this.speed = 1;
            }
            person.setCondition(this);
            stage = 2;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }
}
