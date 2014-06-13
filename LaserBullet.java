public class LaserBullet extends Weapon {
    public LaserBullet(double X, double Y, double userdx, double userdy,
        double userMaxVelocity, double userAccelRate, double ang,
        int userShipID) {
        super(10*userMaxVelocity);
        name = "LaserBullet";
        shipID = userShipID;
        friendlyFire = false;
        fireRateInverse = 35;
        damage = 10;
        x = X;
        y = Y;
        // dx = userdx + Math.cos(ang) * 30;
        // dy = userdy + Math.sin(ang) * 30;

        // Modify this to change velocity. (No joke, VELOCITY. Bigger number,
        // lower velocity.)
        timeToLive = 100;
        // Modify the integers to change RANGE.
        dx = userdx + Math.cos(ang) * 350 / timeToLive;
        dy = userdy + Math.sin(ang) * 350 / timeToLive;
        // It's more realistic to not modify acceleration, but that makes the
        // weapon really hard to use...
        accelRate = -.01;

        // timeToLive = 300;
        // dx = userdx + Math.cos(ang) * 2;
        // dy = userdy + Math.sin(ang) * 2;
        // accelRate = -.02;

        angle = ang;
        isAccel = 1;
        diam = 5;
    }
}
