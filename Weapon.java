public class Weapon extends SpaceObj {
	public static final int LB = 0; // LaserBullet

	public boolean friendlyFire;
	public double range;
	public int damage;
	public double distanceCovered;
	public int fireRateInverse; // the lower the faster; minimum 1
	public Weapon(double mv) {
		super(mv);
		distanceCovered = 0;
		selectedWeapon = -1;
	}
}