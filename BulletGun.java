public class BulletGun extends Weapon {
    
    private BulletGun() {}

    public BulletGun(SpaceObj owner, int reloadTime, double ammo,
        double velocity, int ttl, int damage, double mass,
        boolean friendlyFire, boolean isTurret) {
        
        this.owner = owner;
        setAmmo(ammo);
        setReloadTime(reloadTime);
        reloadProgress = getReloadTime();
        setVelocity(velocity);
        setTTL(ttl);
        this.damage = damage;
        setMass(mass);
        this.friendlyFire = friendlyFire;
        this.isTurret = isTurret;
    }

    public static Weapon newDefaultWeapon(SpaceObj s) {
        return new BulletGun(s, 60, Double.POSITIVE_INFINITY, 3, 60, 10, 0.5, false, false);
    }

    public Projectile generate() {
        ++reloadProgress;
        if (owner.firing && reloadProgress > getReloadTime()) {
            reloadProgress = 0;
            return new Bullet(owner.x, owner.y, owner.dx, owner.dy,
                owner.angle, owner.shipID, getVelocity(), getTTL(),
                damage, getMass(), friendlyFire);
        }
        return new ProjectileBlank();
    }
}
