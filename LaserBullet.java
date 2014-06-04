public class LaserBullet extends Weapon {
	public LaserBullet(double X, double Y, double userdx, double userdy, double userMaxVelocity, double userAccelRate, double ang, int userShipID) {
		super(10*userMaxVelocity);
		// THINK OF A BETTER NAME!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		name = "LaserBullet";
		shipID = userShipID;
		friendlyFire = false;
		fireRateInverse = 5;
		damage = 10;
		x = X;
		y = Y;
		dx = userdx + Math.cos(ang) * 30;
		dy = userdy + Math.sin(ang) * 30;
		range = 600 + Math.sqrt(dx*dx + dy*dy);
		accelRate = 0;
		diam = 1;
	}
}