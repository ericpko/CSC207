import javafx.scene.paint.Color;
import java.util.Queue;
import java.util.LinkedList;

/**
 * A chicken on a farm.
 */
public class Chicken extends Animal {

    // Instance variables
    /** All the eggs laid by Chicken's. Max 20 eggs. */
    protected static Queue<Egg> eggs;

    private Food targetFood;


    // Constructor
    /**
     * Constructs a new Chicken.
     *
     * @param x the x-coordinate of this chicken.
     * @param y the y-coordinate of this chicken.
     */
    public Chicken(int x, int y) {
        super("/'/>", Color.RED, x, y,
                true, true);
        Chicken.eggs = new LinkedList<>();
        this.targetFood = null;
    }

    /**
     * Lays an egg by this chicken and adds it to the list of eggs.
     */
    private void layEgg() {
        Egg egg = new Egg(getX(), getY());
        eggs.add(egg);
    }

    /**
     * Changes the current horizontal direction of this chicken
     * and updates it's appearance.
     */
    protected void turnHorizontal() {
        if (isMovingRight()) {
            super.setAppearance("<\\'\\");
        } else {
            super.setAppearance("/'/>");
        }
        // move in the opposite direction.
        setMovingRight(!isMovingRight());
    }

    /**
     * Changes the current vertical direction of this chicken.
     */
    protected void turnVertical() {
        // move in the opposite direction
        setMovingForward(!isMovingForward());
    }

    /**
     * Makes this chicken move and sometimes
     * change it's direction.
     */
    @Override
    public void makeMove() {

        // Make the move.
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

        // Check to see if this chicken wants to turn around.
        double d = Math.random();

        if (d < 0.1) {
            if (eggs.size() < 20) {
                layEgg();
            }
            turnHorizontal();

        } else if (d < 0.2) {
            digestFood();
            turnVertical();
        }
    }


}
