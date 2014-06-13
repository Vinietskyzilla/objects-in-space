//
//    Objects in Space
//
//    By David Winiecki
//      January 2011
//

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

// Contains the frame, all panels, and all non-SpaceObj classes.
public class Game {
    // Window frame.
    JFrame frame;
    // Desired width for the square in which you fly.
    int panelWidth;
    // Desired height for the square in which you fly.
    int panelHeight;
    // Width of the side menu during game play.
    int sideMenuWidth;

    // Map dimension stuff.

    // Distance between the minimap and the sides of the frame.
    int mapBorder;
    // Width of the minimap. Also used for height of the minimap.
    int mapWidth;

    // Panels, lists, etc.

    // Holds a start next level button. Will someday contain upgrades for the
    // player's ship.
    UpdatePanel up;
    // The panel on which the game is played.
    ActionPanel actP;
    // Tracks the current level of the game.
    int level;
    // Contains all data for active space simulation game level.
    LiveSystem paintLS;
    LiveSystem physicsLS;
    public class GameMutex { public GameMutex() {} }
    GameMutex LiveSystemCopyMutex = new GameMutex();
    // Creates and starts a new game.
    public static void main(String[] args) {
        new Game();
    }
    // Initializes frame and panel dimensions and starts the game by running
    // the UpdatePanel up.
    public Game() {
        int horizantalWindowBorderOffset = 6;
        int headerWindowBorderOffset = 28;
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        // The grey dividing bar cuts out 4 of these pixels.
        sideMenuWidth = 300;
        mapWidth = 200;
        mapBorder = (sideMenuWidth - 4 - mapWidth) / 2;

        // Full screen.
        panelWidth = dim.width - sideMenuWidth;
        panelHeight = dim.height;

        // Set dimensions manually.

        // Don't set height or width greater than the smallest screen size you
        // expect will use this.
        // panelWidth = 1200;
        // panelHeight = 700;

        // Nearly full screen.
        // panelWidth = dim.width - horizantalWindowBorderOffset;
        // panelHeight = dim.height - headerWindowBorderOffset;

        // Big auto square.
        //panelWidth = panelHeight = Math.min(dim.width, dim.height) - 100;

        int windowX = (dim.width / 2) - ((panelWidth + sideMenuWidth) / 2)
          - (horizantalWindowBorderOffset / 2);
        int windowY = (dim.height / 2) - (panelHeight / 2)
          - (headerWindowBorderOffset - 3);

        // Ensure the window header bar is visible.
        // NOT COMPATIBLE with the above full screen option.
        // if(windowX < 0)
        //     windowX = 0;
        // if(windowY < 0)
        //     windowY = 0;

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
    // Contains the run method that executes the game. Created in a new thread
    // by up's button listener.
    public class Play implements Runnable {
        // Initializes state for the next level, displays actP, the ActionPanel
        // on which the game is played, and then cycles through a game loop
        // that updates positions, checks collisions, repaints, etc.

        // Passes control back to up, the UpdatePanel, when the level is
        // complete by calling actP.nextPanel() (ActionPanel.nextPanel()).
        public void run() {
            paintLS = new LiveSystem(level);
            physicsLS = new LiveSystem(paintLS);
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

                physicsLS.updatePhysics();

                synchronized (LiveSystemCopyMutex) {
                  paintLS = physicsLS;
                  physicsLS = new LiveSystem(paintLS);
                }

                actP.repaint();
                if(paintTime >= 16) {
                    paintTime = 0;
                    if(physicsLS.enemiesRemain() == false
                        || physicsLS.hero.status == ShipStatus.DEAD) {
                        ++endLevelPauseCount;
                    }
                }
                try {
                    // Make sure that the game runs at the same speed on all
                    // systems.
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

    public class UpdatePanel extends JPanel {

        public UpdatePanel() {
            this.setMinimumSize(new Dimension(panelWidth, panelHeight));
            JButton buttonStartLevel = new JButton("Start next level");
            buttonStartLevel.addActionListener(new StartLevelListener());
            this.add(buttonStartLevel);
        }

        protected class StartLevelListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().remove(up);
                frame.repaint();
                new Thread(new Play()).start();
            }
        }
    }

    public class ActionPanel extends JPanel {

        public ActionPanel() {
            level = 1;
            myKeyListener mkl = new myKeyListener();
            this.addKeyListener(mkl);
            this.setFocusable(true);
        }

        public void paintComponent(Graphics g) {
            synchronized (LiveSystemCopyMutex) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.black);
                g2d.fillRect(0, 0, panelWidth, panelHeight);
                List<Ship> ships = paintLS.ships;
                PlayerShip hero = paintLS.hero;
                List<Weapon> projectiles = paintLS.projectiles;
                ArrayList<Dust> dust = paintLS.dust;
                ArrayList<Star> stars = paintLS.stars;
                int thisSystemWidth = paintLS.thisSystemWidth;
                int thisSystemHeight = paintLS.thisSystemHeight;
                // Draw stars and dust.
                for(Star s : stars) {
                    g2d.setColor(s.color);
                    g2d.fillOval((int) (s.x + panelWidth / 2),
                        (int) (-(s.y) + panelHeight / 2), s.diam, s.diam);
                }
                for(Dust s : dust) {
                    g2d.setColor(s.color);
                    g2d.fillOval((int) (s.x + panelWidth / 2),
                        (int) (-(s.y) + panelHeight / 2), s.diam, s.diam);
                }
                // Draw ships and weapons.
                AffineTransform origXform;
                AffineTransform newXform;
                int xRot;
                int yRot;
                int frameX;
                int frameY;
                for(Ship s : ships) {
                    origXform = g2d.getTransform();
                    newXform = (AffineTransform)(origXform.clone());
                    // The center of rotation is the position of the object in
                    // the panel.
                    xRot = ((int) s.x) + panelWidth / 2;
                    yRot = -((int) s.y) + panelHeight / 2;
                    newXform.rotate(-s.angle, xRot, yRot);
                    g2d.setTransform(newXform);
                    // Position at which to draw in the panel.
                    frameX = ((int) s.x) + panelWidth / 2
                      - s.origObjImg.getWidth(this)/2;
                    frameY = -((int) s.y) + panelHeight / 2
                      - s.origObjImg.getHeight(this)/2;
                    // Draw rotated image.
                    g2d.drawImage(s.origObjImg, frameX, frameY, this);
                    g2d.setTransform(origXform);
                }
                for(Weapon w : projectiles) {
                    g2d.setColor(Color.yellow);
                    g2d.fillOval((int) (w.x + panelWidth / 2 - w.diam / 2),
                        (int) (-(w.y) + panelHeight / 2 - w.diam / 2),
                        w.diam, w.diam);
                }

                if (hero.countDown > Ship.COUNTDOWN_END) {
                    origXform = g2d.getTransform();
                    newXform = (AffineTransform)(origXform.clone());
                    // The center of rotation is the position of the hero in
                    // the panel.
                    xRot = ((int) hero.x) + panelWidth/2;
                    yRot = -((int) hero.y) + panelHeight/2;
                    newXform.rotate(-hero.angle, xRot, yRot);
                    g2d.setTransform(newXform);
                    // Position at which to draw in the panel.
                    frameX = ((int) hero.x) + panelWidth / 2
                      - hero.origObjImg.getWidth(this)/2;
                    frameY = -((int) hero.y) + panelHeight / 2
                      - hero.origObjImg.getHeight(this)/2;
                    // Draw rotated image.
                    g2d.drawImage(hero.origObjImg, frameX, frameY, this);
                    g2d.setTransform(origXform);
                }

                // Paint sidebar.
                g2d.setColor(Color.black);
                g2d.fillRect(panelWidth, 0, sideMenuWidth, panelHeight);
                g2d.setColor(new Color(0xFF808080));
                g2d.fillRect(panelWidth, 0, 4, panelHeight);

                // Paint minimap.
                g2d.setColor(Color.red);
                g2d.drawRect(panelWidth + 4 + mapBorder - 1, mapBorder - 1,
                    mapWidth + 2, mapWidth + 2);

                // All these large calculations should be stored in variables
                // before each new level begins to save some computations. (At
                // least the parts of the calculations that don't depend on
                // current position of the object.)
                for(Ship s : ships) {
                    // This if statement is extra for later.
                    // if(s.isHostile)
                    g2d.setColor(Color.red);
                    // else
                    // g2d.setColor(Color.green);
                    g2d.fillOval(((int) (s.x * (double) mapWidth / (double) thisSystemWidth) + (mapWidth / 2) + panelWidth + mapBorder + 4) - 1,
                        -((int) (s.y * (double) mapWidth / (double) thisSystemHeight)) + (mapWidth / 2) + mapBorder - 1,
                        3, 3);
                }
                g2d.setColor(Color.blue);
                g2d.fillOval((int) (hero.x * (double) mapWidth / (double) thisSystemWidth) + (mapWidth / 2) + panelWidth + mapBorder + 4 - 1,
                    -((int) (hero.y * (double) mapWidth / (double) thisSystemHeight)) + (mapWidth / 2) + mapBorder - 1,
                    3, 3);
                // Paint rectangle around visible field on minimap.
                g2d.setColor(new Color(0xFF00C000));
                g2d.drawRect((int) ((-panelWidth / 2) * (double) mapWidth / (double) thisSystemWidth) + (mapWidth / 2) + panelWidth + mapBorder + 4,
                    -((int) ((panelHeight / 2) * (double) mapWidth / (double) thisSystemHeight)) + (mapWidth / 2) + mapBorder,
                    (int) (panelWidth * (double) mapWidth / (double) thisSystemWidth),
                    (int) (panelHeight * (double) mapWidth / (double) thisSystemHeight));
            }
        }

        public void nextPanel() {
            frame.getContentPane().remove(this);
            frame.getContentPane().add(BorderLayout.CENTER, up);
            frame.getContentPane().validate();
            frame.getContentPane().repaint();
        }

        public class myKeyListener extends KeyAdapter {
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                synchronized (LiveSystemCopyMutex) {
                    PlayerShip hero = physicsLS.hero;
                    if (hero.status == ShipStatus.ALIVE) {
                        if(keyCode == KeyEvent.VK_LEFT) {
                            hero.turningLeft = true;
                        } else if(keyCode == KeyEvent.VK_RIGHT) {
                            hero.turningRight = true;
                        } else if(keyCode == KeyEvent.VK_UP) {
                            hero.playerIsAccel = true;
                        } else if (keyCode == KeyEvent.VK_Z) {
                            hero.isBoosting = true;
                        } else if(keyCode == KeyEvent.VK_SPACE) {
                            if(hero.firing == 0)
                                hero.firing = 1;
                        }
                        // DEBUG
                        else if(keyCode == KeyEvent.VK_M) {
                            if(physicsLS.centerSpaceObj instanceof PlayerShip)
                                physicsLS.makeCenter(physicsLS.ships.get(0));
                            else
                                physicsLS.makeCenter(physicsLS.hero);
                        }

                        if ((hero.playerIsAccel || hero.isBoosting)
                            && hero.isAccel == 0) {
                            hero.isAccel = 1;
                        }
                    }
                }
                if(keyCode == KeyEvent.VK_ESCAPE)
                    System.exit(0);

                e.consume();
            }

            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                synchronized (LiveSystemCopyMutex) {
                    PlayerShip hero = physicsLS.hero;
                    if (hero.status == ShipStatus.ALIVE) {
                        if (keyCode == KeyEvent.VK_UP) {
                            hero.playerIsAccel = false;
                        } else if (keyCode == KeyEvent.VK_Z) {
                            hero.isBoosting = false;
                        } else if (keyCode == KeyEvent.VK_LEFT) {
                            hero.turningLeft = false;
                        } else if (keyCode == KeyEvent.VK_RIGHT) {
                            hero.turningRight = false;
                        } else if(keyCode == KeyEvent.VK_SPACE) {
                            hero.firing = 0;
                        }

                        if (!hero.playerIsAccel && !hero.isBoosting)
                            hero.isAccel = 0;
                    }
                }
                e.consume();
            }
        }
    }
}
