import javafx.scene.paint.Color;
import java.util.LinkedList;
import java.util.Queue;

public class Food extends FarmObject {

    /** Maximum 20. */
    protected static Queue<Food> animalFood = new LinkedList<>();


    // Constructor
    /**
     * Creates new farm food.
     *
     * @param x the x-coordinate of this food.
     * @param y the y-coordinate of this food.
     */
    public Food(double x, double y) {
        super("%", Color.ORANGE, x, y);
    }


    /**
     * Adds some food to this class.
     *
     * @param food  the animal food to add.
     */
    public static void add(Food food) {
        if (animalFood.size() < 20) {
            Food.animalFood.add(food);
        }
    }

    /**
     * Moves this food horizontally by +/- 1
     * based off the value of Wind.getBlowingForward.
     */
    public void blowFoodVertical() {
        setCoordinates(getX(), getY() + Wind.getBlowingForward());
    }

    /**
     * Moves this food vertically by +/- 1
     * based off the value of Wind.getBlowingRight.
     */
    public void blowFoodHorizontal() {
        setCoordinates(getX() + Wind.getBlowingRight(), getY());
    }


}
