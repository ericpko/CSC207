import javafx.scene.paint.Color;

/**
 * A pig on a farm.
 */
public class Pig extends Animal {


    /**
     * Construct a new Pig.
     */
    public Pig(int x, int y) {
        super("(8):", Color.PINK.darker(), x, y,
                true, false);
    }

    // Methods
    /**
     * Changes the current horizontal direction and appearance of this fat pig.
     */
    protected void turnHorizontal() {
        if (isMovingRight()) {
            super.setAppearance("(8):");
        } else {
            super.setAppearance(":(8)");
        }
        setMovingRight(!isMovingRight());
    }

    /**
     * Changes the current vertical direction of this pig.
     */
    protected void turnVertical() {
        setMovingForward(!isMovingForward());
    }

    /**
     * Makes a move and updates the coordinates of this pig.
     */
    public void makeMove() {

        // Make 2D the move.
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

        // Check to see if this pig should change directions.
        double d = Math.random();
        if (d < 0.1) {
            turnAround();
        } else if (d < 0.2) {
            digestFood();
        }
    }

}
