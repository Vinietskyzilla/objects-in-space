public abstract class Weapon {

    public int reloadTime;
    protected int reloadProgress;
    public double ammo;
    public boolean isTurret;
    public SpaceObj owner;

    public double velocity;
    public int ttl;
    public int damage;
    public double mass;
    public boolean friendlyFire;

    public double range() {
        return velocity * ttl;
    }

    abstract Projectile generate();
}
