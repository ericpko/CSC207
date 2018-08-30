import javafx.scene.paint.Color;

/**
 * A cow on a farm.
 */
public class Cow extends Animal {


    // Constructor
    /**
     * Constructs a new Cow.
     *
     * @param x the x-coordinate of this cow.
     * @param y the y-coordinate of this cow.
     */
    public Cow(int x, int y) {
        super("MOO", Color.BLACK, x, y,
                true, false);
    }


    // Methods
    /**
     * Changes the current horizontal direction of this cow
     * and changes it's appearance.
     */
    protected void turnHorizontal() {
        if (isMovingRight()) {
            super.setAppearance("OOM");
        } else {
            super.setAppearance("MOO");
        }
        // move in the opposite direction.
        setMovingRight(!isMovingRight());
    }

    /**
     * Changes the current vertical direction of this cow.
     */
    protected void turnVertical() {
        // move in the opposite direction
        setMovingForward(!isMovingForward());
    }

    /**
     * Moves this cow and sometimes changes it's direction.
     */
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

        // Check to see if this cow wants to turn around.
        double d = Math.random();

        if (d < 0.1) {
            turnAround();
        } else if (d < 0.2) {
            digestFood();
        }
    }

}
