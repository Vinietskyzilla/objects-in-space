import java.util.*;
import java.util.List;

public class LiveSystem {
    // Width of the current system. Objects that exceed the system bounds are moved to the opposite side of the system. (The system is a torus.)
    int thisSystemWidth;
    // Height of the current system. Objects that exceed the system bounds are moved to the opposite side of the system. (The system is a torus.)
    int thisSystemHeight;
    // Player's ship.
    PlayerShip hero;
    // Contains all ships.
    List<Ship> ships;
    // Contains all projectiles.
    List<Weapon> projectiles;
    // Contains positions, diameters, and colors of non-interactive background stars.
    ArrayList<Star> stars;
    // Contains positions, diameters, and color of non-interactive foreground dust.
    ArrayList<Dust> dust;
    // Used by the collision checker to prevent ships from shooting themselves.
    int shipID;
    // A reference to the SpaceObj on which to center the window when playing the game.
    SpaceObj centerSpaceObj;

    protected LiveSystem() {}

    public LiveSystem(int level) {
        shipID = 1;
        thisSystemWidth = 10000;
        thisSystemHeight = 10000;
       
        hero = new PlayerShip(0, 0, shipID);
        ships = new LinkedList<Ship>();
        projectiles = new LinkedList<Weapon>();
        stars = new ArrayList<Star>();
        dust = new ArrayList<Dust>();
        
        spawnShips(level);
        populateStars(stars);
        populateDust(dust);
        makeCenter(hero); 
    }
    
    public LiveSystem(LiveSystem obj) {
        thisSystemWidth = obj.thisSystemWidth;
        thisSystemHeight = obj.thisSystemHeight;
        hero = obj.hero;
        // Shallow copy of all objects in list.
        ships = new LinkedList<Ship>(obj.ships);
        projectiles = new LinkedList<Weapon>(obj.projectiles);
        stars = new ArrayList<Star>(obj.stars);
        dust = new ArrayList<Dust>(obj.dust);
        shipID = obj.shipID;
        centerSpaceObj = obj.centerSpaceObj;
    }

    public void updatePhysics() {
        fireWeapons();
        expireProjectiles();
        updatePositions();
        checkCollisions();
        kill();
    }      

    public boolean enemiesRemain() {
        return ships.size() > 0 ? true : false;
    }

    public void populateStars(ArrayList<Star> s) {
        for(int i = 0; i < 200; i++) {
            s.add(new Star(4000, 4000));
        }
    }

    public void populateDust(ArrayList<Dust> s) {
        for(int i = 0; i < 150; i++) {
            s.add(new Dust(4000, 4000));
        }
    }

    private void spawnShips(int level) {
        Fighter f;
        f = new Fighter((int)(Math.random() * 4000)-2000, (int)(Math.random() * 4000)-2000, getNextShipID());
        f.maxVelocity = (f.shipID + level) / 3;
        ships.add(f);
        f = new Fighter((int)(Math.random() * 4000)-2000, (int)(Math.random() * 4000)-2000, getNextShipID());
        f.maxVelocity = (f.shipID + level) / 3;
        ships.add(f);
        f = new Fighter((int)(Math.random() * 4000)-2000, (int)(Math.random() * 4000)-2000, getNextShipID());
        f.maxVelocity = (f.shipID + level) / 3;
        ships.add(f);
        f = new Fighter((int)(Math.random() * 4000)-2000, (int)(Math.random() * 4000)-2000, getNextShipID());
        f.maxVelocity = (f.shipID + level) / 3;
        ships.add(f);
        f = new Fighter((int)(Math.random() * 4000)-2000, (int)(Math.random() * 4000)-2000, getNextShipID());
        f.maxVelocity = (f.shipID + level) / 3;
        ships.add(f);
        f = new Fighter((int)(Math.random() * 4000)-2000, (int)(Math.random() * 4000)-2000, getNextShipID());
        f.maxVelocity = (f.shipID + level) / 3;
        ships.add(f);
        f = new Fighter((int)(Math.random() * 4000)-2000, (int)(Math.random() * 4000)-2000, getNextShipID());
        f.maxVelocity = (f.shipID + level) / 3;
        ships.add(f);
        f = new Fighter((int)(Math.random() * 4000)-2000, (int)(Math.random() * 4000)-2000, getNextShipID());
        f.maxVelocity = (f.shipID + level) / 3;
        ships.add(f);
        f = new Fighter((int)(Math.random() * 4000)-2000, (int)(Math.random() * 4000)-2000, getNextShipID());
        f.maxVelocity = (f.shipID + level) / 3;
        ships.add(f);
        f = new Fighter((int)(Math.random() * 4000)-2000, (int)(Math.random() * 4000)-2000, getNextShipID());
        f.maxVelocity = (f.shipID + level) / 3;
        ships.add(f);
    }

    public int getNextShipID() {
        return ++shipID;
    }

    public void fireWeapons() {
        Weapon w;
        int shipsSize = ships.size();
        for(Ship s : ships) {
            w = getWeapon(s);
            if(s.firing == 1) {
                // fire!
                projectiles.add(w);
            }
            if(s.firing > 0)
                ++s.firing;
            if(s.firing > w.fireRateInverse)
                s.firing = 1;
        }
        w = getWeapon(hero);
        if(hero.firing == 1) {
            projectiles.add(w);
        }
        if(hero.firing > 0)
            ++hero.firing;
        if(hero.firing > w.fireRateInverse)
            hero.firing = 1;
    }

    public Weapon getWeapon(Ship s) {
        switch(s.selectedWeapon) {
            //case Weapon.LB:
            //    return new LaserBullet(...);
            //    break;
            default:
                return new LaserBullet(s.x, s.y, s.dx, s.dy, s.maxVelocity, s.accelRate, s.angle, s.shipID);
        }
    }

    private void expireProjectiles() {
        Iterator<Weapon> it = projectiles.iterator();
        while (it.hasNext()) {
            Weapon w = it.next();
            ++w.cyclesLived;
            if(w.cyclesLived >= w.timeToLive)
                it.remove();
        }
    }

    private void updatePositions() {

        ///////////////////////
        // modify rotation
        if(hero.turningLeft) {
            hero.angle += hero.turnRate;
            if(hero.angle > 2*Math.PI)
                hero.angle -= 2*Math.PI;
        }
        if(hero.turningRight) {
            hero.angle -= hero.turnRate;
            if(hero.angle < 0)
                hero.angle += 2*Math.PI;
        }

        for(Ship s : ships) {
            if(s.turningLeft) {
                s.angle += s.turnRate;
                if(s.angle > 2*Math.PI)
                    s.angle -= 2*Math.PI;
            }
            if(s.turningRight) {
                s.angle -= s.turnRate;
                if(s.angle < 0)
                    s.angle += 2*Math.PI;
            }
        }

        /////////////////////////////////
        // modify velocity and position
        for(Ship s : ships) {
            intervalAccel(s);
            s.x += s.dx;
            s.y += s.dy;
        }
        for(Weapon w : projectiles) {
            intervalAccel(w);
            w.x += w.dx;
            w.y += w.dy;
        }
        intervalAccel(hero);
        // Bring speed back down from boosting. (Don't
        // give extra speed for free.)
        if (!hero.isBoosting) {
            if (hero.dx > hero.maxVelocity)
              --hero.dx;
            else if (hero.dx < -hero.maxVelocity)
              ++hero.dx;
            if (hero.dx > hero.maxVelocity)
              --hero.dx;
            else if (hero.dx < -hero.maxVelocity)
              ++hero.dx;
        }
        hero.x += hero.dx;
        hero.y += hero.dy;

        // move everything based on movement of centerSpaceObj
        for(Ship s : ships) {
            if (s != centerSpaceObj) {
                s.x -= centerSpaceObj.dx;
                s.y -= centerSpaceObj.dy;
                if(s.x < -thisSystemWidth/2)
                    s.x += thisSystemWidth;
                else if(s.x > thisSystemWidth/2)
                    s.x -= thisSystemWidth;
                if(s.y < -thisSystemHeight/2)
                    s.y += thisSystemHeight;
                else if(s.y > thisSystemHeight/2)
                    s.y -= thisSystemHeight;
            }
        }
        for(Weapon w : projectiles) {
            w.x -= centerSpaceObj.dx;
            w.y -= centerSpaceObj.dy;
            if(w.x < -thisSystemWidth/2)
                w.x += thisSystemWidth;
            else if(w.x > thisSystemWidth/2)
                w.x -= thisSystemWidth;
            if(w.y < -thisSystemHeight/2)
                w.y += thisSystemHeight;
            else if(w.y > thisSystemHeight/2)
                w.y -= thisSystemHeight;
        }
        if (hero != centerSpaceObj) {
            hero.x -= centerSpaceObj.dx;
            hero.y -= centerSpaceObj.dy;
            if(hero.x < -thisSystemWidth/2)
                hero.x += thisSystemWidth;
            else if(hero.x > thisSystemWidth/2)
                hero.x -= thisSystemWidth;
            if(hero.y < -thisSystemHeight/2)
                hero.y += thisSystemHeight;
            else if(hero.y > thisSystemHeight/2)
                hero.y -= thisSystemHeight;
        }
        
        centerSpaceObj.x -= centerSpaceObj.dx;
        centerSpaceObj.y -= centerSpaceObj.dy;

        for(Star s : stars) {
            // change by movement divided by 3 so that stars move slowly and seem far away
            // (Parallaxing!)
            s.x -= centerSpaceObj.dx / 3;
            s.y -= centerSpaceObj.dy / 3;
            if(s.x < -2000)
                s.x += 4000;
            else if(s.x > 2000)
                s.x -= 4000;
            if(s.y < -2000)
                s.y += 4000;
            else if(s.y > 2000)
                s.y -= 4000;
        }
        for(Dust s : dust) {
            // have space dust move past at full speed
            s.x -= centerSpaceObj.dx;
            s.y -= centerSpaceObj.dy;
            if(s.x < -2000)
                s.x += 4000;
            else if(s.x > 2000)
                s.x -= 4000;
            if(s.y < -2000)
                s.y += 4000;
            else if(s.y > 2000)
                s.y -= 4000;
        }
    }

    public void intervalAccel(SpaceObj s) {
        if(s.isAccel == 1)
            s.accelerate();
        if(s.isAccel > 0)
            ++s.isAccel;
        if(s.isAccel > 4)
            s.isAccel = 1;
    }

    public void checkCollisions() {
        for(Ship s : ships) {
            if(s.alive) {
                Iterator<Weapon> it = projectiles.iterator();
                while (it.hasNext()) {
                    Weapon w = it.next();
                    if(w.friendlyFire || (w.shipID != s.shipID)) {
                        boolean hit = s.y - s.diam / 2 <= w.y &&
                            s.y + s.diam / 2 >= w.y &&
                            s.x + s.diam / 2 >= w.x &&
                            s.x - s.diam / 2 <= w.x;
                        if(hit) {
                            s.structInteg -= w.damage;
                            if(s.structInteg <= 0) {
                                s.countDown = 170;
                                s.alive = false;
                                s.die();
                            }
                            it.remove();
                        }
                    }
                }
            }
        }
        Ship s = hero;
        if (hero.alive) {
            Iterator<Weapon> it = projectiles.iterator();
            while (it.hasNext()) {
                Weapon w = it.next();
                if(w.friendlyFire || (w.shipID != s.shipID)) {
                    boolean hit = s.y - s.diam / 2 <= w.y &&
                        s.y + s.diam / 2 >= w.y &&
                        s.x + s.diam / 2 >= w.x &&
                        s.x - s.diam / 2 <= w.x;
                    if(hit) {
                        s.structInteg -= w.damage;
                        if(s.structInteg <= 0) {
                            s.countDown = 170;
                            s.alive = false;
                            s.die();
                            ships.add(hero);
                        }
                        it.remove();
                    }
                }
            }
        }
    }

    public void kill() {
        Ship s;
        for(int i = 0; i < ships.size(); i++) {
            s = ships.get(i);
            if(s.countDown > 0) {
                --s.countDown;
            }
            if(s.countDown == 1) {
                ships.remove(s);
                --i;
            }
        }
    }

    public void makeCenter(SpaceObj s) {
        centerSpaceObj = s;

        double x = s.x, y = s.y;

        int shipsSize = ships.size();
        for (int i = 0; i < ships.size(); i++) {
            translateWithCenter(ships.get(i), x, y);
        }
        int projectilesSize = projectiles.size();
        for (int i = 0; i < projectiles.size(); i++) {
            translateWithCenter(projectiles.get(i), x, y);
        }
        translateWithCenter(hero, x, y);
        // Should also translate dust and stars for a better effect.
        // (Make it really feel like we are looking at a different
        // part of space.)
    }

    public void translateWithCenter(SpaceObj s, double x, double y) {
        s.x -= x;
        s.y -= y;
        if(s.x < -thisSystemWidth/2)
            s.x += thisSystemWidth;
        else if(s.x > thisSystemWidth/2)
            s.x -= thisSystemWidth;
        if(s.y < -thisSystemHeight/2)
            s.y += thisSystemHeight;
        else if(s.y > thisSystemHeight/2)
            s.y -= thisSystemHeight;
    }
}
