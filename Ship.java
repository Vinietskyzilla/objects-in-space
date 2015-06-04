import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;

// By moving weapons from SpaceObj to Ship we optimize a little all the
// SpaceObj's that don't use weapons. Profiling showed that allocating the
// weapons was a significant extra overhead for projectile objects, for
// example, which are constantly created and destroyed.

public abstract class Ship extends SpaceObj {
    public static final int COUNTDOWN_END = 0;
    public static final int COUNTDOWN_INIT = 170;
    public int countDown;
    public ShipStatus status;
    public ArrayList<Weapon> weapons;
    // Index of selected weapon.
    public int selectedWeapon;
    protected Ship() { }
    public Ship(double mv) {
        super(mv);
        countDown = COUNTDOWN_INIT;
        status = ShipStatus.ALIVE;
        weapons = new ArrayList<Weapon>();
        selectedWeapon = 0;
    }
    public void die() {
        isAccel = 0;
        isBoosting = false;
        turningRight = false;
        turningLeft = false;
        firing = false;
        // If the ship is not motionless, which would screw up the angle
        // calculation, then calculate the new angle.
        if (dx != 0 || dy != 0) {
            // East is 0, west is -pi. Pi is outside the range of directions.
            // I think Math.atan returns theta in the range {-pi/2, pi/2}. (I
            // think it can differentiate between straight down and straight
            // up because Java has Infinity and -Infinity.)
            angle = Math.atan(dy/dx);
            if (dx < 0) {
                if (angle >= 0)
                    angle -= Math.PI;
                else
                    angle += Math.PI;
            }
        }
        try {
            origObjImg =
              ImageIO.read(getClass().getResource("ballofflame.png"));
        } catch (IOException e) { e.printStackTrace(); }
        status = ShipStatus.DYING;
    }
}
