import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;

/**
 * Animal manure
 */
public class Manure extends FarmObject {

    // Instance variables
    /** The age of this manure. */
    private long manureAge;
    /** A list of all manure produced. Maximum 20. */
    protected static List<Manure> manureList = new LinkedList<>();


    // Constructor
    /**
     * Creates new manure.
     *
     * @param x the x-coordinate of this manure.
     * @param y the y-coordinate of this manure.
     */
    public Manure(double x, double y) {
        super("*", Color.BROWN.darker().darker(), x, y);
        this.manureAge = System.currentTimeMillis();
    }


    /**
     * Adds some manure to this class.
     *
     * @param poop  the animal manure to add.
     */
    public static void add(Manure poop) {
        if (manureList.size() < 20) {
            Manure.manureList.add(poop);
        }

    }

    public long getManureAge() {
        return manureAge;
    }


}
