//
//    Objects in Space
//
//    By David Winiecki
//      January 2011
//
/**
    @version Objects in Space
    @author David Winiecki
*/
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

/**
    Contains the frame, all panels, and all non-SpaceObj classes.
*/
public class Game {
    /**
        Window frame.
    */
    JFrame frame;
    /**
        Desired width for the square in which you fly.
    */
    int panelWidth; // desired width for the square in which you fly
    /**
        Desired height for the square in which you fly.
    */
    int panelHeight; // desired height for the square in which you fly
    /**
        Width of the side menu during game play.
    */
    int sideMenuWidth;
    /**
        Width of the current system. Objects that exceed the system bounds are moved to the opposite side of the system. (The system is a torus.)
    */
    int thisSystemWidth;
    /**
        Height of the current system. Objects that exceed the system bounds are moved to the opposite side of the system. (The system is a torus.)
    */
    int thisSystemHeight;
    // map dimension stuff
    /**
        Distance between the minimap and the sides of the frame
    */
    int mapBorder;
    /**
        Width of the minimap. Also used for height of the minimap.
    */
    int mapWidth;
    // panels, lists, etc.
    /**
        Holds a start next level button. Will someday contain upgrades for the player's ship.
    */
    UpdatePanel up;
    /**
        The panel on which the game is played.
    */
    ActionPanel actP;
    /**
        Player's ship.
    */
    PlayerShip hero;
    /**
        Contains all ships.
    */
    List<Ship> ships = Collections.synchronizedList(new LinkedList<Ship>());
    /**
        Contains all projectiles.
    */
    List<Weapon> projectiles = Collections.synchronizedList(new LinkedList<Weapon>());
    /**
        Contains positions, diameters, and colors of non-interactive background stars.
    */
    ArrayList<Star> stars = new ArrayList<Star>();
    /**
        Contains positions, diameters, and color of non-interactive foreground dust.
    */
    ArrayList<Dust> dust = new ArrayList<Dust>();
    /**
        Tracks the current level of the game.
    */
    int level;
    /**
        Used by the collision checker to prevent ships from shooting themselves.
    */
    int shipID = 2;
    /**
        A reference to the SpaceObj on which to center the window when playing the game.
    */
    SpaceObj centerSpaceObj;
    /**
        Creates and starts a new game.
    */
    public static void main(String[] args) {
        new Game();
    }
    /**
        Initializes frame and panel dimensions and starts the game by running the UpdatePanel up.
    */
    public Game() {
        int horizantalWindowBorderOffset = 6;
        int headerWindowBorderOffset = 28;
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        sideMenuWidth = 300; // the grey dividing bar cuts out 4 of these pixels
        mapWidth = 200;
        mapBorder = (sideMenuWidth - 4 - mapWidth) / 2;

        /////////////////////////////////////////////////////////////////////////////////////////////
        //
        // BEGIN: THIS STUFF NEEDS SERIOUS TESTING

        // full screen
        panelWidth = dim.width - sideMenuWidth;
        panelHeight = dim.height;
        //System.out.println(panelWidth);

        // set dimensions manually
        // don't set height or width greater than the smallest screen size you expect will use this
        // panelWidth = 1200;
        // panelHeight = 700;

        // nearly full screen
        // panelWidth = dim.width - horizantalWindowBorderOffset;
        // panelHeight = dim.height - headerWindowBorderOffset;

        // big auto square
        //panelWidth = panelHeight = Math.min(dim.width, dim.height) - 100;

        int windowX = (dim.width / 2) - ((panelWidth + sideMenuWidth) / 2) - (horizantalWindowBorderOffset / 2);
        int windowY = (dim.height / 2) - (panelHeight / 2) - (headerWindowBorderOffset - 3);

        // if you want to ensure the window header bar is visible
        // NOT COMPATIBLE with the above full screen option
        // if(windowX < 0)
            // windowX = 0;
        // if(windowY < 0)
            // windowY = 0;

        // END: THIS STUFF NEEDS SERIOUS TESTING
        //
        /////////////////////////////////////////////////////////////////////////////////////////////

        frame.setLayout(null);
        frame.setBounds(windowX,
            windowY,
            panelWidth + sideMenuWidth + horizantalWindowBorderOffset,
            panelHeight + headerWindowBorderOffset);
        frame.setResizable(false);
        up = new UpdatePanel();
        up.setBounds(0, 0, panelWidth + sideMenuWidth, panelHeight);
        actP = new ActionPanel();
        actP.setBounds(0, 0, panelWidth + sideMenuWidth, panelHeight);
        frame.getContentPane().add(up);
        frame.setVisible(true);
    }
    /**
        Contains the run method that executes the game. Created in a new thread by up's button listener.
    */
    public class Play implements Runnable {
        /**
            Initializes state for the next level, displays actP, the ActionPanel on which the game is played, and then cycles through a game loop that updates positions, checks collisions, repaints, etc.

            Passes control back to up, the UpdatePanel, when the level is complete by calling actP.nextPanel() (ActionPanel.nextPanel()).
        */
        public void run() {
            shipID = 2;
            thisSystemWidth = 10000;
            thisSystemHeight = 10000;
            stars.clear();
            populateStars(stars);
            dust.clear();
            populateDust(dust);
            ships.clear();
            projectiles.clear();
            spawnShips();
            hero.turningRight = false;
            hero.turningLeft = false;
            hero.isAccel = 0;
            hero.dx = 0;
            hero.dy = 0;
            hero.angle = .5*Math.PI;
            hero.firing = 0;
            hero.structInteg = hero.structIntegInit;
            frame.getContentPane().add(actP);
            frame.getContentPane().validate();
            frame.getContentPane().repaint();
            actP.requestFocusInWindow();
            boolean levelComplete = false;
            int endLevelPauseCount = 0;
            double countTo = 300;
            long loopTime = new Date().getTime();
            long paintTime = 16;
            while(levelComplete == false) {
                long msStart = new Date().getTime();
                fireWeapons();
                expireProjectiles();
                updatePositions();
                checkCollisions();
                kill();
                actP.repaint();
                if(paintTime >= 16) {
                    paintTime = 0;
                    if(enemiesRemain() == false)
                        ++endLevelPauseCount;
                }
                try {
                    // Make sure that the game runs at the same speed on all systems
                    msStart = 4 - (new Date().getTime() - msStart);
                    if(msStart > 0)
                        Thread.sleep(msStart);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(endLevelPauseCount >= countTo)
                    levelComplete = true;
                paintTime += new Date().getTime() - loopTime;
                loopTime = new Date().getTime();
            }
            ++level;
            actP.nextPanel();
        }
    }
    /**
    
    */
    public boolean enemiesRemain() {
        return ships.size() > 0 ? true : false;
    }
    /**

    */
    public void populateStars(ArrayList<Star> s) {
        for(int i = 0; i < 200; i++) {
            s.add(new Star(4000, 4000));
        }
    }
    /**

    */
    public void populateDust(ArrayList<Dust> s) {
        for(int i = 0; i < 150; i++) {
            s.add(new Dust(4000, 4000));
        }
    }
    /**

    */
    private void spawnShips() {
        ships.add(new Fighter(100, 100, getNextShipID() + level));
        ships.add(new Fighter(-100, -100, getNextShipID() + level));
        ships.add(new Fighter(-200, 200, getNextShipID() + level * 2));
    }
    /**

    */
    public int getNextShipID() {
        return shipID++;
    }
    /**

    */
    public void fireWeapons() {
        Weapon w;
        int shipsSize = ships.size();
        synchronized(ships) {
            for(int i = 0; i < shipsSize; i++) {
                Ship s = ships.get(i);
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
    }
    /**

    */
    public Weapon getWeapon(Ship s) {
        switch(s.selectedWeapon) {
            //case Weapon.LB:
            //    return new LaserBullet(...);
            //    break;
            default:
                return new LaserBullet(s.x, s.y, s.dx, s.dy, s.maxVelocity, s.accelRate, s.angle, s.shipID);
        }
    }
    /**

    */
    private void expireProjectiles() {
        int projectilesSize = projectiles.size();
        synchronized (projectiles) {
            for(int i = 0; i < projectilesSize; i++) {
                Weapon w = projectiles.get(i);
                ++w.cyclesLived;
                if(w.cyclesLived >= w.timeToLive) {
                    projectiles.remove(w);
                    --projectilesSize;
                }
            }
        }
    }
    /**

    */
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

        int shipsSize = ships.size();
        for(int i = 0; i < shipsSize; i++) {
            Ship s = ships.get(i);

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
        for(int i = 0; i < shipsSize; i++) {
            Ship s = ships.get(i);
            intervalAccel(s);
            s.x += s.dx;
            s.y += s.dy;
        }
        int projectilesSize = projectiles.size();
        for(int i = 0; i < projectilesSize; i++) {
            Weapon w = projectiles.get(i);
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
        for(int i = 0; i < shipsSize; i++) {
            Ship s = ships.get(i);
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
        for(int i = 0; i < projectilesSize; i++) {
            Weapon w = projectiles.get(i);
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
    /**

    */
    public void intervalAccel(SpaceObj s) {
        if(s.isAccel == 1)
            s.accelerate();
        if(s.isAccel > 0)
            ++s.isAccel;
        if(s.isAccel > 4)
            s.isAccel = 1;
    }
    /**

    */
    public void checkCollisions() {
        Weapon w;
        Ship s;
        boolean hit;
        for(int i = 0; i < ships.size(); i++) {
            s = ships.get(i);
            if(s.alive) {
                for(int j = 0; j < projectiles.size(); j++) {
                    w = projectiles.get(j);
                    if(w.friendlyFire || (w.shipID != s.shipID)) {
                        hit = s.y - s.diam / 2 <= w.y &&
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
                            projectiles.remove(w);
                            --j;
                        }
                    }
                }
            }
        }
        s = hero;
        for(int j = 0; j < projectiles.size(); j++) {
            w = projectiles.get(j);
            if(w.friendlyFire || (w.shipID != s.shipID)) {
                hit = s.y - s.diam / 2 <= w.y &&
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
                    projectiles.remove(w);
                    --j;
                }
            }
        }
    }
    /**

    */
    public void kill() {
        Ship s;
        synchronized (ships) {
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
    }
    /**

    */
    public void makeCenter(SpaceObj s) {
        centerSpaceObj = s;

        double x = s.x, y = s.y;

        int shipsSize = ships.size();
        synchronized (ships) {
            for (int i = 0; i < ships.size(); i++) {
                translateWithCenter(ships.get(i), x, y);
            }
        }
        int projectilesSize = projectiles.size();
        synchronized (projectiles) {
            for (int i = 0; i < projectiles.size(); i++) {
                translateWithCenter(projectiles.get(i), x, y);
            }
        }
        translateWithCenter(hero, x, y);
        // Should also translate dust and stars for a better effect.
        // (Make it really feel like we are looking at a different
        // part of space.)
    }
    /**

    */
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
    /**

    */
    public class UpdatePanel extends JPanel {
        /**

        */
        public UpdatePanel() {
            this.setMinimumSize(new Dimension(panelWidth, panelHeight));
            JButton buttonStartLevel = new JButton("Start next level");
            buttonStartLevel.addActionListener(new StartLevelListener());
            this.add(buttonStartLevel);
        }
        /**

        */
        protected class StartLevelListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().remove(up);
                frame.repaint();
                new Thread(new Play()).start();
            }
        }
    }
    /**

    */
    public class ActionPanel extends JPanel {
        /**

        */
        public ActionPanel() {
            level = 1;
            hero = new PlayerShip(0, 0, 1);
            makeCenter(hero);
            myKeyListener mkl = new myKeyListener();
            this.addKeyListener(mkl);
            this.setFocusable(true);
        }
        /**

        */
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.black);
            g2d.fillRect(0, 0, panelWidth, panelHeight);
            /////////////////////////
            // DRAW STARS AND DUST
            for(Star s : stars) {
                g2d.setColor(s.color);
                g2d.fillOval((int) (s.x + panelWidth / 2), (int) (-(s.y) + panelHeight / 2), s.diam, s.diam);
            }
            for(Dust s : dust) {
                g2d.setColor(s.color);
                g2d.fillOval((int) (s.x + panelWidth / 2), (int) (-(s.y) + panelHeight / 2), s.diam, s.diam);
            }
            /////////////////////////
            // DRAW SHIPS AND WEAPONS
            AffineTransform origXform;
            AffineTransform newXform;
            int xRot;
            int yRot;
            int frameX;
            int frameY;
            synchronized (ships) {
                Ship s;
                for(int i = 0; i < ships.size(); i++) {
                    s = ships.get(i);
                    origXform = g2d.getTransform();
                    newXform = (AffineTransform)(origXform.clone());
                    //center of rotation is position of object in the panel
                    xRot = ((int) s.x) + panelWidth / 2;
                    yRot = -((int) s.y) + panelHeight / 2;
                    newXform.rotate(-s.angle, xRot, yRot);
                    g2d.setTransform(newXform);
                    //draw rotated image
                    frameX = ((int) s.x) + panelWidth / 2 - s.getImage().getWidth(this)/2;
                    frameY = -((int) s.y) + panelHeight / 2 - s.getImage().getHeight(this)/2;
                    g2d.drawImage(s.getImage(), frameX, frameY, this);
                    g2d.setTransform(origXform);
                }
            }
            synchronized (projectiles) {
                Weapon s;
                for(int i = 0; i < projectiles.size(); i++) {
                    s = projectiles.get(i);
                    g2d.setColor(Color.yellow);
                    g2d.fillOval((int) (s.x + panelWidth / 2 - s.diam / 2), (int) (-(s.y) + panelHeight / 2 - s.diam / 2), s.diam, s.diam);
                }
            }

            origXform = g2d.getTransform();
            newXform = (AffineTransform)(origXform.clone());
            //center of rotation is center of the panel
            xRot = ((int) hero.x) + panelWidth/2;
            yRot = -((int) hero.y) + panelHeight/2;
            newXform.rotate(-hero.angle, xRot, yRot);
            g2d.setTransform(newXform);
            //draw image centered in panel
            frameX = ((int) hero.x) + panelWidth / 2 - hero.getImage().getWidth(this)/2;
            frameY = -((int) hero.y) + panelHeight / 2 - hero.getImage().getHeight(this)/2;
            g2d.drawImage(hero.getImage(), frameX, frameY, this);
            g2d.setTransform(origXform);

            /////////////////////////
            // PAINT SIDEBAR
            g2d.setColor(Color.black);
            g2d.fillRect(panelWidth, 0, sideMenuWidth, panelHeight);
            g2d.setColor(new Color(0xFF808080));
            g2d.fillRect(panelWidth, 0, 4, panelHeight);

            /////////////////////////
            // PAINT MINIMAP
            g2d.setColor(Color.red);
            g2d.drawRect(panelWidth + 4 + mapBorder - 1, mapBorder - 1, mapWidth + 2, mapWidth + 2);

            // / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /
            // All these large calculations should be stored in variables before
            //     each new level begins to save some computations. (At least the parts of the
            //     calculations that don't depend on current position of the object.)
            // / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /

            synchronized (ships) {
                for(int i = 0; i < ships.size(); i++) {
                    // this if statement is extra for later
                    // if(s.isHostile)
                    g2d.setColor(Color.red);
                    // else
                    // g2d.setColor(Color.green);
                    g2d.fillOval(((int) (ships.get(i).x * (double) mapWidth / (double) thisSystemWidth) + (mapWidth / 2) + panelWidth + mapBorder + 4) - 1, -((int) (ships.get(i).y * (double) mapWidth / (double) thisSystemHeight)) + (mapWidth / 2) + mapBorder - 1, 3, 3);
                }
            }
            g2d.setColor(Color.blue);
            g2d.fillOval((int) (hero.x * (double) mapWidth / (double) thisSystemWidth) + (mapWidth / 2) + panelWidth + mapBorder + 4 - 1, -((int) (hero.y * (double) mapWidth / (double) thisSystemHeight)) + (mapWidth / 2) + mapBorder - 1, 3, 3);
            // paint rectangle around visible field on minimap
            g2d.setColor(new Color(0xFF00C000));
            g2d.drawRect((int) ((-panelWidth / 2) * (double) mapWidth / (double) thisSystemWidth) + (mapWidth / 2) + panelWidth + mapBorder + 4,
                -((int) ((panelHeight / 2) * (double) mapWidth / (double) thisSystemHeight)) + (mapWidth / 2) + mapBorder,
                (int) (panelWidth * (double) mapWidth / (double) thisSystemWidth),
                (int) (panelHeight * (double) mapWidth / (double) thisSystemHeight));
        }
        /**

        */
        public void nextPanel() {
            frame.getContentPane().remove(this);
            frame.getContentPane().add(BorderLayout.CENTER, up);
            frame.getContentPane().validate();
            frame.getContentPane().repaint();
        }
        /**

        */
        public class myKeyListener extends KeyAdapter {
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if(keyCode == KeyEvent.VK_LEFT)
                    hero.turningLeft = true;
                if(keyCode == KeyEvent.VK_RIGHT)
                    hero.turningRight = true;
                if(keyCode == KeyEvent.VK_UP)
                    if(hero.isAccel == 0)
                        hero.isAccel = 1;
                if (keyCode == KeyEvent.VK_Z) {
                    if(hero.isAccel == 0)
                        hero.isAccel = 1;
                    hero.isBoosting = true;
                }

                if(keyCode == KeyEvent.VK_SPACE) {
                    if(hero.firing == 0)
                        hero.firing = 1;
                }
                // DEBUG
                if(keyCode == KeyEvent.VK_M)
                    if(centerSpaceObj instanceof PlayerShip)
                        makeCenter(ships.get(0));
                    else
                        makeCenter(hero);

                if(keyCode == KeyEvent.VK_ESCAPE)
                    System.exit(0);

                e.consume();
            }
            /**

            */
            public void keyTyped(KeyEvent e) {
                // if(e. == 'a') {
                // }
                // else if(e. == 's') {
                // }
                // else if(e. == 'd') {
                // }
                // else if(e. == 'f') {
                // }
            }
            /**

            */
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_UP) {
                    hero.isAccel = 0;
                }
                else if (keyCode == KeyEvent.VK_Z) {
                    hero.isAccel = 0;
                    hero.isBoosting = false;
                }
                // else if (keyCode == KeyEvent.VK_DOWN)
                else if (keyCode == KeyEvent.VK_LEFT) {
                    hero.turningLeft = false;
                }
                else if (keyCode == KeyEvent.VK_RIGHT) {
                    hero.turningRight = false;
                }
                else if(keyCode == KeyEvent.VK_SPACE) {
                    hero.firing = 0;
                }
                e.consume();
            }
        }
    }
}
