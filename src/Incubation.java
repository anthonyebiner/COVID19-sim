import javafx.scene.paint.Color;

import java.util.Random;

public class Incubation extends Condition {
    private int time;
    private Person person;
    private double mortality;
    private Random rand = new Random();
    private int stage = 0;

    public Incubation(Person person) {
        this.dead = false;
        this.speed = 2;
        this.incubation = 0;
        this.color = Color.LIGHTSALMON;
        this.person = person;
        mortality = 0.03;
    }

    public Incubation(double speed, Person p) {
        this(p);
        this.speed = speed;
    }

    public void infect(Person p) {
        if (p.getCondition() instanceof Healthy)
            p.setCondition(new Incubation(p.getCondition().speed, p));
    }

    public void advance() {
        time += 1;
        if (time > Settings.FRAMERATE * 3) {
            person.setCondition(new Sick(person));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }
}
