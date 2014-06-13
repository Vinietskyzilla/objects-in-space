public class BulletGun extends Weapon {
    
    protected BulletGun() {}

    public BulletGun(SpaceObj owner, int reloadTime, double ammo,
        double velocity, int ttl, int damage, double mass,
        boolean friendlyFire, boolean isTurret) {
        
        this.owner = owner;
        this.ammo = ammo;
        this.reloadTime = reloadTime;
        reloadProgress = reloadTime;
        this.velocity = velocity;
        this.ttl = ttl;
        this.damage = damage;
        this.mass = mass;
        this.friendlyFire = friendlyFire;
        this.isTurret = isTurret;
    }

    public static Weapon newDefaultWeapon(SpaceObj s) {
        return new BulletGun(s, 60, Double.POSITIVE_INFINITY, 3, 60, 10, 0.5, false, false);
    }

    public Projectile generate() {
        ++reloadProgress;
        if (owner.firing && reloadProgress > reloadTime) {
            reloadProgress = 0;
            return new Bullet(owner.x, owner.y, owner.dx, owner.dy,
                owner.angle, owner.shipID, velocity, ttl, damage, mass,
                friendlyFire);
        }
        return new ProjectileBlank();
    }
}
