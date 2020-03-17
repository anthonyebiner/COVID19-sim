import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board extends Application {
    private int width = Settings.WIDTH;
    private int height = Settings.HEIGHT;
    private int border = Settings.BORDER;

    List<Person> people = new ArrayList<>();

    Random rand = new Random();

    Pane peopleLayer;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("sun.java2d.opengl", "true");
        Group root = new Group();

        peopleLayer = new Pane();
        root.getChildren().add(peopleLayer);

        Scene scene = new Scene(root, width, height, Color.WHITE);
        stage.setScene(scene);

        stage.show();

        loadPeople(Settings.NUMHEALTHY, Settings.NUMSICK, Settings.DISTANCING);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(Settings.FRAMERATE), event -> {
            checkCollisions();
            for (Person person: people) {
                person.advance();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void loadPeople(int numHealthy, int numSick, double distancing) {
        for (int i=0; i<numHealthy; i+=1) {
            if (rand.nextDouble() < distancing)
                people.add(new Person(new Healthy(0.1),
                        randRange(border, width-border),
                        randRange(border, height-border),
                        rand.nextInt(361)));
            else
                people.add(new Person(new Healthy(),
                        randRange(border, width-border),
                        randRange(border, height-border),
                        rand.nextInt(361)));
            peopleLayer.getChildren().add(people.get(i));
        }
        for (int i=numHealthy; i<numHealthy + numSick; i+=1) {
            people.add(new Person(new Healthy(),
                    randRange(border, width-border),
                    randRange(border, height-border),
                    rand.nextInt(361)));
            people.get(i).setCondition(new Covid(people.get(i)));
            peopleLayer.getChildren().add(people.get(i));
        }
    }

    private int randRange(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    private void checkCollisions() {
        System.out.println();
        for (int i= 0; i<people.size(); i+=1) {
            Person p1 = people.get(i);
            for (int j=i+1; j<people.size(); j+=1) {
                Person p2 = people.get(j);
                if (p1.intersects(p2)) {
                    if (p2.getDirVector().getLength() > 0.11 && p1.getDirVector().getLength() > 0.11) {
                        Vector2D delta = new Vector2D(p2.get_x() - p1.get_x(), p2.get_y() - p1.get_y());
                        double d = delta.getLength();
                        Vector2D mtd = delta.getMultiplied(((p2.getRadius() + p1.getRadius())-d)/d);
                        mtd.multiply(1.0/2);
                        p1.moveAbs(p1.get_x() - mtd.x, p1.get_y() - mtd.y);
                        p2.moveAbs(p2.get_x() + mtd.x, p2.get_y() + mtd.y);
                        mtd.multiply(2);
                        Vector2D v = new Vector2D(p2.getDirVector().x - p1.getDirVector().x, p2.getDirVector().y - p1.getDirVector().y);
                        double vn = v.dot(mtd.getNormalized());
                        if (vn > 0.0f) return;
                        double k = (-(1.0f + 1) * vn) / (2);
                        Vector2D impulse = mtd.getNormalized().getMultiplied(k);
                        p1.setDirVector(p1.getDirVector().getSubtracted(impulse));
                        p2.setDirVector(p2.getDirVector().getAdded(impulse));
                    } else if (p2.getDirVector().getLength() < 0.11){
                        Vector2D delta = new Vector2D(p2.get_x() - p1.get_x(), p2.get_y() - p1.get_y());
                        double d = delta.getLength();
                        Vector2D mtd = delta.getMultiplied(((p2.getRadius() + p1.getRadius())-d)/d);
                        mtd.multiply(1.0/2);
                        p1.moveAbs(p1.get_x() - mtd.x, p1.get_y() - mtd.y);
                        p2.moveAbs(p2.get_x() + mtd.x, p2.get_y() + mtd.y);
                        p1.setDirVector(p1.getDirVector().getRotatedBy(Math.toDegrees(180)));
                    } else {
                        Vector2D delta = new Vector2D(p2.get_x() - p1.get_x(), p2.get_y() - p1.get_y());
                        double d = delta.getLength();
                        Vector2D mtd = delta.getMultiplied(((p2.getRadius() + p1.getRadius())-d)/d);
                        mtd.multiply(1.0/2);
                        p1.moveAbs(p1.get_x() - mtd.x, p1.get_y() - mtd.y);
                        p2.moveAbs(p2.get_x() + mtd.x, p2.get_y() + mtd.y);
                        p2.setDirVector(p2.getDirVector().getRotatedBy(Math.toDegrees(180)));
                    }
                    if (!p1.getCondition().equals(p2.getCondition()))
                        p1.infect(p2);
                        p2.infect(p1);
                }
            }
            if (p1.get_x() > width - 10 || p1.get_x() < 10) {
                p1.setDirVector(p1.getDirVector().get_yFlipped());
                p1.advance();
            }
            if (p1.get_y() > height - 10 || p1.get_y() < 10) {
                p1.setDirVector(p1.getDirVector().get_xFlipped());
                p1.advance();
            }
        }
    }
}