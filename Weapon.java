/**

*/
public class Weapon extends SpaceObj {
  /**

  */
  public static final int LB = 0; // LaserBullet

  /**

  */
  public boolean friendlyFire;
  /**

  */
  public double timeToLive;
  /**

  */
  public int damage;
  /**

  */
  public double cyclesLived;
  /**

  */
  public int fireRateInverse; // the lower the faster; minimum 1
  /**

  */
  public Weapon(double mv) {
    super(mv);
    cyclesLived = 0;
  }
}
