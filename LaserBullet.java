public class LaserBullet extends Weapon {
    public LaserBullet(double X, double Y, double userdx, double userdy,
        double userMaxVelocity, double userAccelRate, double ang,
        int userShipID) {
        super(0);
        double weaponVelocity = 3;
        maxVelocity = userMaxVelocity + weaponVelocity;
        name = "LaserBullet";
        shipID = userShipID;
        friendlyFire = false;
        fireRateInverse = 60;
        damage = 10;
        mass = 0.5;
        x = X;
        y = Y;
        // Modify this to change RANGE.
        timeToLive = 60;
        dx = userdx + Math.cos(ang) * weaponVelocity;
        dy = userdy + Math.sin(ang) * weaponVelocity;
        baseAccelRate = 0;
        angle = ang;
        isAccel = 1;
        diam = 5;
    }
}
