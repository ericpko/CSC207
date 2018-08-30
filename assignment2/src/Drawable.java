import javafx.scene.canvas.GraphicsContext;

/**
 * A drawable object.
 */
public interface Drawable {


    // **** Methods ****
    /**
     * Draws the given string s in the given graphics context
     * at at the given cursor location.
     *
     * @param g the graphics context in which to draw the string.
     * @param s the string to draw.
     * @param x the x-coordinate of the string's cursor location.
     * @param y the y-coordinate of the string's cursor location.
     */
    void drawString(GraphicsContext g, String s, double x, double y);


    /**
     * Draws this farm pen item.
     *
     * @param g the graphics context in which to draw this item.
     */
    void draw(GraphicsContext g);

//    /**
//     * Sets the appearance of this drawable object.
//     *
//     * @param s The appearance of this drawable object.
//     */
//    void setAppearance(String s);
//
//    /**
//     * Gets the appearance of this drawable object.
//     *
//     * @return  The string appearance of this drawable object.
//     */
//    String getAppearance();
}
