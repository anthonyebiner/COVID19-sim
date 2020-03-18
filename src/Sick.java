import javafx.scene.paint.Color;

import java.util.Random;

public class Sick extends Condition {
    private int time;
    private Person person;
    private double mortality;
    private Random rand = new Random();

    public Sick(Person person) {
        this.dead = false;
        this.speed = 0.1;
        this.color = Color.RED;
        this.person = person;
        mortality = 0.05;
    }

    public Sick(double speed, Person p) {
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
            if (rand.nextDouble() < mortality) {
                person.setCondition(new Deceased(person));
            } else {
                if (rand.nextDouble() < Settings.DISTANCING)
                    person.setCondition(new Immune(person, speed));
                else
                    person.setCondition(new Immune(person));
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }
}
