public class Bullet extends Projectile {
    public Bullet(
        double x,
        double y,
        double dx,
        double dy,
        double angle,
        int shipID,
        double velocity,
        int ttl,
        int damage,
        double mass,
        boolean friendlyFire) {

        super(Double.POSITIVE_INFINITY);
        
        this.x = x;
        this.y = y;
        this.dx = dx + Math.cos(angle) * velocity;
        this.dy = dy + Math.sin(angle) * velocity;
        this.angle = angle;
        this.shipID = shipID;
        this.velocity = velocity;
        this.ttl = ttl;
        this.damage = damage;
        this.mass = mass;
        this.friendlyFire = friendlyFire;
        
        baseAccelRate = 0;
        isAccel = 1;
        diam = 5;
    }
}
