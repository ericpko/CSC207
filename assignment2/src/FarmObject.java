import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;

/**
 * An object on a farm.
 */
public abstract class FarmObject implements Drawable {

    // Instance variables
    /** The appearance of this object. */
    private String appearance;

    /** The color of this object. */
    private Color color;

    /** Coordinates of this object */
    private Point2D coordinates;


    // Constructors
    /**
     * Create a new FarmObject.
     *
     * @param appearance    the appearance of this FarmObject.
     * @param color the color of this FarmObject.
     * @param x the x-coordinate of this FarmObject.
     * @param y the y-coordinate of this FarmObject.
     */
    public FarmObject(String appearance, Color color, double x, double y) {
        this.appearance = appearance;
        this.color = color;
        this.coordinates = new Point2D(x, y);
    }

    /**
     * Create a new FarmObject
     *
     * @param appearance    the appearance of this FarmObject
     * @param color the color of this FarmObject.
     */
    public FarmObject(String appearance, Color color) {
        this.appearance = appearance;
        this.color = color;
        this.coordinates = new Point2D(0, 0);
    }


    // Getters
    /**
     *
     * @return  The appearance of this object.
     */
    protected String getAppearance() {
        return appearance;
    }

    /**
     *
     * @return  The color of this object
     */
    protected Color getColor() {
        return color;
    }

    /**
     * Return the (x, y) coordinates of this Point2D.
     *
     * @return  The (x, y) coordinates of this Point2D.
     */
    protected Point2D getCoordinates() {
        return coordinates;
    }

    /**
     * Gets the x-coordinate.
     *
     * @return  the x-coordinate of this point.
     */
    protected double getX() {
        return coordinates.getX();
    }

    /**
     * Gets the y-coordinate.
     *
     * @return  the y-coordinate of this point.
     */
    protected double getY() {
        return coordinates.getY();
    }


    // Setters
    /**
     * Sets the appearance of this object.
     *
     * @param appearance the appearance of this object.
     */
    protected void setAppearance(String appearance) {
        this.appearance = appearance;
    }

    /**
     * Sets the color of this object.
     *
     * @param color the color of this object.
     */
    protected void setColor(Color color) {
        this.color = color;
    }

    /**
     * Set the (x, y) coordinates of this object.
     *
     * @param x     The x coordinate of this object.
     * @param y     The y coordinate of this object.
     */
    protected void setCoordinates(double x, double y) {
        if (x < 10) {
            x = 10;
        } else if (x > 80) {
            x = 80;
        }
        if (y < 10) {
            y = 10;
        } else if (y > 100) {
            y = 100;
        }
        this.coordinates = new Point2D(x, y);
    }


    // Methods
    @Override
    public void drawString(GraphicsContext g, String s, double x, double y) {
        g.setFill(color);
        g.fillText(s, x * 10, y * 6);
    }


    @Override
    public void draw(GraphicsContext g) {
        drawString(g, appearance, coordinates.getX(), coordinates.getY());
    }

}
