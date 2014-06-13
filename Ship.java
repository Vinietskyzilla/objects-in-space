import java.util.*;

// By moving weapons from SpaceObj to Ship we optimize a little all the
// SpaceObj's that don't use weapons. Profiling showed that allocating the
// weapons was a significant extra overhead for projectile objects, for
// example, which are constantly created and destroyed.

public class Ship extends SpaceObj {
    public static final int COUNTDOWN_END = 0;
    public static final int COUNTDOWN_INIT = 170;
    public int countDown;
    public ShipStatus status;
    public ArrayList<Integer> weapons;
    public int selectedWeapon; // index of selected weapon
    protected Ship() {}
    public Ship(double mv) {
        super(mv);
        countDown = COUNTDOWN_INIT;
        status = ShipStatus.ALIVE;
        weapons = new ArrayList<Integer>();
        selectedWeapon = 0;
    }
}
