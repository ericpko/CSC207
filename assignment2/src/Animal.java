import javafx.scene.paint.Color;

/**
 * An abstract animal.
 */
public abstract class Animal extends FarmObject implements Movable {

    // Instance variables
    /** True iff this object is moving to the right. */
    private boolean movingRight;

    /** True iff this object is moving forward. */
    private boolean movingForward;



    // Constructor
    /**
     * Creates a new farm animal.
     *
     * @param appearance    the appearance of this farm animal.
     * @param color the color of this farm animal.
     * @param x the x-coordinate of this farm animal.
     * @param y the y-coordinate of this farm animal.
     * @param movingRight   true iff this farm animal is moving to the right.
     * @param movingForward true iff this farm animal is moving to the left.
     */
    public Animal(String appearance, Color color, double x, double y,
                  boolean movingRight, boolean movingForward) {
        super(appearance, color, x, y);
        this.movingRight = movingRight;
        this.movingForward = movingForward;
    }

    /**
     * Creates a new farm animal.
     *
     * @param appearance    the appearance of this farm animal.
     * @param color the color of this farm animal.
     */
    public Animal(String appearance, Color color) {
        super(appearance, color);
        this.movingRight = false;
        this.movingForward = false;
    }


    /**
     *
     * @return  True iff this object is moving to the right.
     */
    protected boolean isMovingRight() {
        return movingRight;
    }

    /**
     *
     * @return  True iff this object is moving forwards.
     */
    protected boolean isMovingForward() {
        return movingForward;
    }


    /**
     * Set the right/left direction of this object.
     *
     * @param movingRight   True iff this object is moving to the right.
     */
    protected void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    /**
     * Set the forwards/backwards direction of this object.
     *
     * @param movingForward     True iff this object is moving forwards.
     */
    protected void setMovingForward(boolean movingForward) {
        this.movingForward = movingForward;
    }

    protected abstract void turnHorizontal();

    protected abstract void turnVertical();

    protected void turnAround() {
        turnHorizontal();
        turnVertical();
    }

    /**
     * Digests the food this animal has eaten.
     *
     * @return  True iff this animal digests the food.
     */
    protected boolean digestFood() {
        Manure poop = new Manure(getX(), getY());
        Manure.add(poop);
        return true;
    }

    /**
     * Moves this object.
     */
    @Override
    public abstract void makeMove();

}
