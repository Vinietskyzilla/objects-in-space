public abstract class Weapon {

    private int reloadTime;
    int reloadProgress;
    private double ammo;
    boolean isTurret;
    SpaceObj owner;

    private double velocity;
    private int ttl;
    int damage;
    private double mass;
    boolean friendlyFire;

    abstract Projectile generate();

    public double range() {
        return velocity * ttl;
    }
    public int setReloadTime(int t) {
        return reloadTime = t > 0 ? t : 1;
    }
    public int getReloadTime() {
        return reloadTime;
    }
    public double setAmmo(double a) {
        return ammo = a >= 0 ? a : 0;
    }
    public double getAmmo() {
        return ammo;
    }
    public double setVelocity(double v) {
        return velocity = v >= 0 ? v : 0;
    }
    public double getVelocity() {
        return velocity;
    }
    public int setTTL(int t) {
        return ttl = t > 0 ? t : 1;
    }
    public int getTTL() {
        return ttl;
    }
    public double setMass(double m) {
        return mass = m > 0 ? m : 0.000000001;
    }
    public double getMass() {
        return mass;
    }
}
