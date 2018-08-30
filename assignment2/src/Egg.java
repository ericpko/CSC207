import javafx.scene.paint.Color;

public class Egg extends FarmObject {


    // Constructor
    /**
     * Creates a new Egg.
     *
     * @param x the x-coordinate of this egg.
     * @param y the y-coordinate of this egg.
     */
    public Egg(double x, double y) {
        super("o", Color.ROSYBROWN, x, y);
    }


}
