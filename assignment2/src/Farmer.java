import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.ArrayList;

/**
 * A farmer on a farm.
 */
public class Farmer extends Animal {

    // Instance variables
    /** A list of all Farmers' farm animals */
    protected static List<Animal> farmAnimals;

    // protected List<Long> manureAge;

    private long timeStart;
    /** The target egg for this farmer to collect. */
    private Egg targetEgg;

    private GraphicsContext g;
    /** A basket this farmer uses to collect eggs. */
    private List<Egg> eggBasket;


    public Farmer(int x, int y) {
        super("farmer", Color.BLACK, x, y,
                true, false);
        Farmer.farmAnimals = new ArrayList<>();
        // this.manureAge = new ArrayList<>();
        this.targetEgg = null;
        this.eggBasket = new ArrayList<>();
    }

    @Override
    protected void turnHorizontal() {
        setMovingRight(!isMovingRight());
    }

    @Override
    protected void turnVertical() {
        // move in the opposite direction.
        setMovingForward(!isMovingForward());
    }

    /**
     * Drops four pieces of food around this human.
     */
    private void dropFood() {
        for (int i = -1; i <= 1; i += 2) {
            for (int j = -1; j <= 1; j += 2) {
                Food food = new Food(getX() + i, getY() + j);
                Food.add(food);
            }
        }
    }

    @Override
    public void draw(GraphicsContext g) {
        g.setFill(Color.SANDYBROWN.darker());
        drawString(g, getAppearance(), getX(), getY());
        g.fillText("Eggs: " + eggBasket.size(), 25, 25);
    }

    @Override
    public void makeMove() {
        if (targetEgg == null) {
            targetEgg = Chicken.eggs.peek();
        }
        if (targetEgg != null) {
            // check if farmer is on an egg.
            if (getCoordinates().equals(targetEgg.getCoordinates())) {
                eggBasket.add(targetEgg);
                Chicken.eggs.remove();
                targetEgg = Chicken.eggs.peek();
                return;
            }
            else {
                // move toward the targetEgg.
                if (getX() > targetEgg.getX()) {
                    setCoordinates(getX() - 1, getY());
                } else if (getX() < targetEgg.getX()) {
                    setCoordinates(getX() + 1, getY());
                }
                if (getY() > targetEgg.getY()) {
                    setCoordinates(getX(), getY() - 1);
                } else if (getY() < targetEgg.getY()) {
                    setCoordinates(getX(), getY() + 1);
                }
            }
        }
        else {
            // egg is still null.
            if (isMovingRight()) {
                setCoordinates(getX() + 1, getY());
            } else {
                setCoordinates(getX() - 1, getY());
            }

            if (isMovingForward()) {
                setCoordinates(getX(), getY() + 1);
            } else {
                setCoordinates(getX(), getY() - 1);
            }
        }

        double d = Math.random();
        if (d < 0.05) {
            dropFood();
        } else if (d < 0.1 && targetEgg == null) {
            turnAround();
        }
    }

}
