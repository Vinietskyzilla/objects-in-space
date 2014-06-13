public class Projectile extends SpaceObj {
    
    public boolean friendlyFire;
    public int ttl;
    public double velocity;
    public int damage;
    public double cyclesLived;
    public Projectile(double maxVelocity) {
        super(maxVelocity);
        cyclesLived = 0;
    }
}
