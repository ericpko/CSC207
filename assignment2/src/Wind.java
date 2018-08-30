/**
 * Wind on a farm.
 */
public class Wind {

    /** The vertical direction of the wind. */
    private static int blowingForward = 0;

    /** The horizontal direction of the wind. */
    private static int blowingRight = 0;


    private static void changeUp() {
        if (blowingForward != 0)
            if (Math.random() <= 0.1) {
            } else if (Math.random() <= 0.3) {
                blowingForward = -1 * blowingForward;
            } else blowingForward = 0;
        else
            // blowingUp was zero.
            if (Math.random() <= 0.1) {
                blowingForward = -1;
            } else if (Math.random() <= 0.1) {
                blowingForward = 1;
            }
    }

    private static void changeRight() {
        if (blowingRight != 0)
            if (Math.random() <= 0.1) {
            } else if (Math.random() <= 0.3) {
                blowingRight = -1 * blowingRight;
            } else blowingRight = 0;
        else
            // blowingRight was zero.
            if (Math.random() <= 0.1) {
                blowingRight = -1;
            } else if (Math.random() <= 0.1) {
                blowingRight = 1;
            }
    }

    public static void changeDirection() {
        changeUp();
        changeRight();
    }

    public static int getBlowingForward() {
        return blowingForward;
    }

    public static int getBlowingRight() {
        return blowingRight;
    }
}
