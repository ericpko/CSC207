import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/** Our take on the "classical" game Farm Ville */
public class Main extends Application implements EventHandler<ActionEvent> {

    private List<Animal> farmAnimals = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("FarmVille");

        Group root = new Group();
        Scene theScene = new Scene(root);
        primaryStage.setScene(theScene);
        Canvas canvas = new Canvas(1024, 720);

        Rectangle r = new Rectangle(50, 50, 900, 600);
        r.setFill(Color.TRANSPARENT);
        r.setStroke(Color.DARKBLUE);
        r.setArcWidth(20);
        r.setArcHeight(20);

        root.getChildren().addAll(canvas, r);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        populateFarm();

        drawShapes(gc);

        Timeline gameLoop = new Timeline();
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        final long timeStart = System.currentTimeMillis();
        // Human.timeStart = timeStart; // why?


        KeyFrame kf = new KeyFrame(Duration.seconds(0.5),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        // handle the animals on this farm
                        for (Animal farmAnimal : farmAnimals) {
                            farmAnimal.makeMove();
                        }
                        // handle the food on this farm
                        for (Food food : Food.animalFood) {

                            // Check if the wind is changing directions
                            Wind.changeDirection();

                            // Blow the food in the direction of the wind.
                            food.blowFoodHorizontal();
                            food.blowFoodVertical();
                        }
                        // handle the manure on this farm.
                        for (int i = 0; i < Manure.manureList.size(); i++) {
                            if (System.currentTimeMillis() -
                                    Manure.manureList.get(i).getManureAge() >= 10000) {
                                Manure.manureList.remove(i);
                            }
                        }
                        // Clear the canvas
                        gc.clearRect(0, 0, 1024, 720);
                        drawShapes(gc);
                    }
                });

        gameLoop.getKeyFrames().add(kf);
        gameLoop.play();
        primaryStage.show();
    }


    private void drawShapes(GraphicsContext gc) {
        // Draw the farm animals.
        for (Animal farmAnimal : farmAnimals) {
            farmAnimal.draw(gc);
        }
        for (Egg egg : Chicken.eggs) {
            egg.draw(gc);
        }
        for (Food food : Food.animalFood) {
            food.draw(gc);
        }
        for (Manure manure : Manure.manureList) {
            manure.draw(gc);
        }
    }

    private void populateFarm() {
        // Add farm animals
        Random random = new Random();

        Animal farmer = new Farmer(0, 0);
        farmAnimals.add(farmer);
        for (int i = 0; i <= 10; i++) {
            farmAnimals.add(new Chicken(random.nextInt(81),
                    random.nextInt(51)));
        }
        for (int i = 0; i <= 2; i++) {
            farmAnimals.add(new Pig(random.nextInt(81),
                    random.nextInt(101)));
            farmAnimals.add(new Cow(random.nextInt(81),
                    random.nextInt(101)));
        }
    }

    @Override
    public void handle(ActionEvent ae) {

        // handle the animals on this farm
        for (Animal farmAnimal : farmAnimals) {
            farmAnimal.makeMove();
        }
        // handle the food on this farm
        for (Food food : Food.animalFood) {

            // Check if the wind is changing directions
            Wind.changeDirection();

            // Blow the food in the direction of the wind.
            food.blowFoodHorizontal();
            food.blowFoodVertical();
        }
        // handle the manure on this farm.
        for (int i = 0; i < Manure.manureList.size(); i++) {
            if (System.currentTimeMillis() -
                    Manure.manureList.get(i).getManureAge() >= 10000) {
                Manure.manureList.remove(i);
            }
        }
        // Clear the canvas
//        gc.clearRect(0, 0, 1024, 720);
//        drawShapes(gc);

    }

}
