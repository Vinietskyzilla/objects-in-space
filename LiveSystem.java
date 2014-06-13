import java.util.*;
import java.util.List;

public class LiveSystem {
    // Width of the current system. Objects that exceed the system bounds are
    // moved to the opposite side of the system. (The system is a torus.)
    int thisSystemWidth;
    // Height of the current system. Objects that exceed the system bounds are
    // moved to the opposite side of the system. (The system is a torus.)
    int thisSystemHeight;
    int viewWidth, viewHeight, starWidth, starHeight;
    // Player's ship.
    PlayerShip hero;
    // Contains all ships.
    List<Ship> ships;
    // Contains all projectiles.
    List<Weapon> projectiles;
    // Contains positions, diameters, and colors of non-interactive background
    // stars.
    ArrayList<Star> stars;
    // Contains positions, diameters, and color of non-interactive foreground
    // dust.
    ArrayList<Dust> dust;
    // Used by the collision checker to prevent ships from shooting themselves.
    int shipID;
    // A reference to the SpaceObj on which to center the window when playing
    // the game.
    SpaceObj centerSpaceObj;

    protected LiveSystem() {}

    public LiveSystem(int level, int viewWidth, int viewHeight) {
        shipID = 1;
        thisSystemWidth = 10000;
        thisSystemHeight = 10000;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        starWidth = 2*viewWidth;
        starHeight = 2*viewHeight;

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
        viewWidth = obj.viewWidth;
        viewHeight = obj.viewHeight;
        starWidth = obj.starWidth;
        starHeight = obj.starHeight;
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
        ai();
    }

    public boolean enemiesRemain() {
        return ships.size() > 0 ? true : false;
    }

    public void populateStars(ArrayList<Star> s) {
        for(int i = 0; i < 50; i++) {
            s.add(new Star(starWidth*2, starHeight*2));
        }
    }

    public void populateDust(ArrayList<Dust> s) {
        for(int i = 0; i < 20; i++) {
            s.add(new Dust(starWidth*2, starHeight*2));
        }
    }

    private void spawnShips(int level) {

        int fighterCount = level + 2;

        for (int i = 0; i < fighterCount; i++) {
            ships.add(new Fighter(
                  (int)(Math.random() * 1000 * fighterCount)
                  - 500 * fighterCount,
                  (int)(Math.random() * 1000 * fighterCount)
                  - 500 * fighterCount,
                  getNextShipID(),
                  1));//level/4.0));
        }
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
                // Fire!
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
                return new LaserBullet(s.x, s.y, s.dx, s.dy, s.maxVelocity,
                    s.baseAccelRate, s.angle, s.shipID);
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

        // Modify rotation.
        if (hero.status == ShipStatus.ALIVE) {
        
            // East is 0, west is -pi. Pi is outside the range of directions.
            if(hero.turningLeft) {
                hero.angle += hero.turnRate;
                if(hero.angle >= Math.PI)
                    hero.angle -= 2*Math.PI;
            }
            if(hero.turningRight) {
                hero.angle -= hero.turnRate;
                if(hero.angle < -Math.PI)
                    hero.angle += 2*Math.PI;
            }
        }

        for(Ship s : ships) {
            if(s.status == ShipStatus.ALIVE) {
                if(s.turningLeft) {
                    s.angle += s.turnRate;
                    if(s.angle >= Math.PI)
                        s.angle -= 2*Math.PI;
                }
                if(s.turningRight) {
                    s.angle -= s.turnRate;
                    if(s.angle < -Math.PI)
                        s.angle += 2*Math.PI;
                }
            }
        }

        // Modify velocity and position.
        for(Ship s : ships) {
            if(s.status == ShipStatus.ALIVE)
                intervalAccel(s);
            s.x += s.dx;
            s.y += s.dy;
        }
        for(Weapon w : projectiles) {
            intervalAccel(w);
            w.x += w.dx;
            w.y += w.dy;
        }
        if(hero.status == ShipStatus.ALIVE)
            intervalAccel(hero);
        // Bring speed back down from boosting. (Don't give extra speed for
        // free.)
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

        // Move everything based on movement of centerSpaceObj.
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

        centerSpaceObj.x = 0;
        centerSpaceObj.y = 0;

        for(Star s : stars) {
            // Change by movement divided by 3 so that stars move slowly and
            // seem far away. (Parallaxing!)
            s.x -= centerSpaceObj.dx / 3;
            s.y -= centerSpaceObj.dy / 3;
            if(s.x < -starWidth / 2)
                s.x += starWidth;
            else if(s.x > starWidth / 2)
                s.x -= starWidth;
            if(s.y < -starHeight / 2)
                s.y += starHeight;
            else if(s.y > starHeight / 2)
                s.y -= starHeight;
        }
        for(Dust s : dust) {
            // Have space dust move past at full speed.
            s.x -= centerSpaceObj.dx;
            s.y -= centerSpaceObj.dy;
            if(s.x < -starWidth / 2)
                s.x += starWidth;
            else if(s.x > starWidth / 2)
                s.x -= starWidth;
            if(s.y < -starHeight / 2)
                s.y += starHeight;
            else if(s.y > starHeight / 2)
                s.y -= starHeight;
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
            if(s.status == ShipStatus.ALIVE) {
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
                                s.status = ShipStatus.DYING;
                                s.die();
                            }
                            w.applyInertia(s);
                            it.remove();
                        }
                    }
                }
            }
        }
        Ship s = hero;
        if (hero.status == ShipStatus.ALIVE) {
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
                            s.status = ShipStatus.DYING;
                            s.die();
                        }
                        w.applyInertia(s);
                        it.remove();
                    }
                }
            }
        }
    }

    public void kill() {
        Iterator<Ship> it = ships.iterator();
        while (it.hasNext()) {
            Ship s = it.next();

            if(s.status == ShipStatus.DYING) {
                --s.countDown;
                if(s.countDown <= Ship.COUNTDOWN_END) {
                    s.status = ShipStatus.DEAD;
                    it.remove();
                }
            }
        }
        if(hero.status == ShipStatus.DYING) {
            --hero.countDown;
            if(hero.countDown <= Ship.COUNTDOWN_END) {
                hero.status = ShipStatus.DEAD;
            }
        }
    }

    public void ai() {

        // Have all ships follow the player's ship.
        for (Ship s : ships) {

            double dx = hero.x - s.x,
                   dy = hero.y - s.y;

            double theta = Math.atan(dy/dx);

            // East is 0, west is -pi. Pi is outside the range of directions.
            
            if (dx < 0 && theta < 0) {
                theta += Math.PI;
            } else if (dx < 0 && theta >= 0) {
                theta -= Math.PI;
            }

            if (s.angle < -Math.PI / 2 && theta > Math.PI / 2) {
                s.turningRight = true;
                s.turningLeft = false;
            } else if (s.angle > Math.PI / 2 && theta < -Math.PI / 2) {
                s.turningRight = false;
                s.turningLeft = true;
            } else if (theta - s.angle > 0.1) {
                s.turningRight = false;
                s.turningLeft = true;
            } else if (theta - s.angle < -0.1) {
                s.turningRight = true;
                s.turningLeft = false;
            } else {
                s.turningRight = false;
                s.turningLeft = false;
            }
        }
    }

    public void makeCenter(SpaceObj newCenter) {
        centerSpaceObj = newCenter;

        double x = newCenter.x, y = newCenter.y;

        for (Ship s : ships) {
            translateWithCenter(s, x, y);
        }
        for (Weapon w : projectiles) {
            translateWithCenter(w, x, y);
        }
        translateWithCenter(hero, x, y);
        // Should also translate dust and stars for a better effect. (Make it
        // really feel like we are looking at a different part of space.)
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
