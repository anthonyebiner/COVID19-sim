import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class Board extends Application {
    private int width = Settings.WIDTH;
    private int height = Settings.HEIGHT;
    private int border = Settings.BORDER;

    List<Person> people = new ArrayList<>();

    Random rand = new Random();

    Pane peopleLayer;
    Pane chartLayer;

    XYChart.Series<Number, Number> healthy;
    XYChart.Series<Number, Number> sick1;
    XYChart.Series<Number, Number> sick2;
    XYChart.Series<Number, Number> immune;
    XYChart.Series<Number, Number> dead;

    private int frame;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("sun.java2d.opengl", "true");
        BorderPane root = new BorderPane();
        peopleLayer = new Pane();

        chartLayer = new Pane();

        final NumberAxis xAxis = new NumberAxis(0, 4000, 100);
        final NumberAxis yAxis = new NumberAxis(0, Settings.NUMHEALTHY + Settings.NUMSICK, (Settings.NUMHEALTHY + Settings.NUMSICK)/10);
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);
        xAxis.setOpacity(0);

        final StackedAreaChart<Number, Number> lineChart = new StackedAreaChart<>(xAxis, yAxis);
        lineChart.setAnimated(false);
        lineChart.setLegendVisible(false);
        lineChart.setHorizontalGridLinesVisible(false);
        lineChart.setVerticalGridLinesVisible(false);
        lineChart.setMaxHeight(75);
        lineChart.setMaxWidth(width - 25);
        lineChart.setMinWidth(width - 25);
        lineChart.setOpacity(0.75);
        lineChart.setCreateSymbols(false);
        lineChart.setHorizontalZeroLineVisible(false);

        BorderPane.setAlignment(lineChart, Pos.CENTER);

        //defining a series to display data
        healthy = new XYChart.Series<>();
        sick1 = new XYChart.Series<>();
        sick2 = new XYChart.Series<>();
        immune = new XYChart.Series<>();
        dead = new XYChart.Series<>();

        // add series to chart
        lineChart.getData().addAll(sick1, sick2, immune, healthy, dead);
        lineChart.getStylesheets().add("chart.css");
        chartLayer.getChildren().add(lineChart);

        root.getChildren().add(peopleLayer);
        root.getChildren().add(chartLayer);


        Scene scene = new Scene(root, width, height, Color.WHITE);
        stage.setScene(scene);

//        stage.show();

        loadPeople(Settings.NUMHEALTHY, Settings.NUMSICK, Settings.DISTANCING);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(Settings.FRAMERATE), event -> {
            try {
                takeSnapShot(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
            checkCollisions();
            loadChart();
            for (Person person: people) {
                person.advance();
            }
        }));
        timeline.setCycleCount(4000);
        timeline.play();
    }

    private int n = 0;

    private void takeSnapShot(Scene scene) throws IOException {
        n += 1;
        System.out.println(n);
        WritableImage writableImage =
                new WritableImage((int)scene.getWidth(), (int)scene.getHeight());
        scene.snapshot(writableImage);

        File file = new File("/home/anthony/IdeaProjects/Corona/gif/" + n + ".png");
        boolean a =ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
        System.out.println(a);
    }

    private void loadChart() {
        int numHealthy = 0;
        int numSick1 = 0;
        int numSick2 = 0;
        int numDead = 0;
        int numImmune = 0;

        for (Person p: people) {
            if (p.getCondition() instanceof Healthy) {
                numHealthy += 1;
            } else if (p.getCondition() instanceof Incubation) {
                numSick1 += 1;
            } else if (p.getCondition() instanceof Sick) {
                numSick2 += 1;
            } else if (p.getCondition() instanceof Deceased) {
                numDead += 1;
            } else if (p.getCondition() instanceof Immune) {
                numImmune += 1;
            }
        }

        healthy.getData().add(new XYChart.Data<>(frame, numHealthy));
        sick1.getData().add(new XYChart.Data<>(frame, numSick1));
        sick2.getData().add(new XYChart.Data<>(frame, numSick2));
        dead.getData().add(new XYChart.Data<>(frame, numDead));
        immune.getData().add(new XYChart.Data<>(frame, numImmune));
        frame += 1;
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
            people.get(i).setCondition(new Incubation(people.get(i)));
            peopleLayer.getChildren().add(people.get(i));
        }
    }

    private int randRange(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    private void checkCollisions() {
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