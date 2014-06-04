// ¤ • · ° * + ¤ · ° * • ' . · ° * ` + ¤ •
//	° *	  ·   Objects in°Space    *     '
//   ¤    ·   *     °   '   *    `     ¤
//	·   ° *   By David·Winiecki  ¤  •   ·
// ° `   ¤  •  *January 2011 ·    °   *   
//   ¤   ·   °   •   ·  °  `   ¤  °  *  `
// * • ' . · ° * ` + ¤ • ¤ • · ° * + ¤ · °
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

// Note to self, or, How I Fixed the astros Concurrency Bug
//
// Not sure how much synchronizing had to do with it.  I finally just went and took out all iterations and then
//   made everything check index bounds repeatedly.  Also, there may still be a problem in the paint code where
//   it tries to call get(i) but i is null and it throws an exception.  Not sure how to fix that to guarantee
//   it never does that, but you could... already did it.  Made it recheck size every time. :P


public class Game {
	JFrame frame;
	int panelWidth;  // desired width for the square in which you fly
	int panelHeight; // desired height for the square in which you fly
	int sideMenuWidth;
	int thisSystemWidth;
	int thisSystemHeight;
	// map dimension stuff
	int mapBorder;
	int mapWidth;
	// panels, lists, etc.
	UpdatePanel up;
	ActionPanel actP;
	PlayerShip hero;
	// java.util.List<SpaceObj> astros = Collections.synchronizedList(new ArrayList<SpaceObj>());
	java.util.List<SpaceObj> astros = new Vector<SpaceObj>();
	ArrayList<Star> stars = new ArrayList<Star>();
	ArrayList<Dust> dust = new ArrayList<Dust>();
	int level;
	int shipID = 2;
	SpaceObj centerSpaceObj;
	//boolean repainterRun;
	public static void main(String[] args) {
		new Game();
		//System.out.println("1");
	}
	public Game() {
		//System.out.println("2");
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
		// frame.getContentPane().add(BorderLayout.CENTER, up);
		frame.getContentPane().add(up);
		// System.out.println("3");
		frame.setVisible(true);
	}
	public class Play implements Runnable {
		public void run() {
			// System.out.println("4");
			shipID = 2;
			thisSystemWidth = 10000;
			thisSystemHeight = 10000;
			stars.clear();
			// populateStars(stars, thisSystemWidth, thisSystemHeight);
			populateStars(stars);
			dust.clear();
			// populateDust(dust, thisSystemWidth, thisSystemHeight);
			populateDust(dust);
			//System.out.println(stars.size() + " stars");
			// for(Star s : stars) {
				// System.out.println("Star at (" + (s.x + panelWidth / 2) + ", " + (-(s.y) + panelHeight / 2) + ")");
			// }
			// System.out.println("5");
			//synchronized (astros) {
				astros.clear();
			//}
			spawnSpaceObjs();
			// System.out.println("7");
			hero.turningRight = false;
			hero.turningLeft = false;
			hero.isAccel = 0;
			hero.dx = 0;
			hero.dy = 0;
			hero.angle = .5*Math.PI;
			hero.firing = 0;
			hero.structInteg = hero.structIntegInit;
			// frame.getContentPane().add(BorderLayout.CENTER, actP);
			frame.getContentPane().add(actP);
			frame.getContentPane().validate();
			frame.getContentPane().repaint();
			actP.requestFocus();
			// System.out.println("8");
			//repainterRun = true;
			//Thread repainterThread = new Thread(new Repainter());
			//repainterThread.start();
			// long totalTime = new Date().getTime();
			boolean levelComplete = false;
			int endLevelPauseCount = 0;
			double countTo = 300;
			long loopTime = new Date().getTime();
			long paintTime = 16;
			while(levelComplete == false) {
				long msStart = new Date().getTime();
				fireWeapons();
				// System.out.println("11");
				updatePositions();
				// System.out.println("13");
				//System.out.println(Math.toDegrees(hero.angle));
				checkCollisions();
				kill();
				if(paintTime >= 16) {
					actP.repaint();
					paintTime = 0;
					if(enemiesRemain() == false)
						++endLevelPauseCount;
				}
				// System.out.println("15");
				//System.out.println(astros.get(0).shipID + " " + astros.get(0).name + " " + astros.get(0).structInteg + " " + astros.get(0).countDown);
				try {
					// Make sure that the game runs at the same speed on all systems
					msStart = 4 - (new Date().getTime() - msStart);
					if(msStart > 0)
						Thread.sleep(msStart);
					//Thread.sleep(4);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(endLevelPauseCount >= countTo)
					levelComplete = true;
				paintTime += new Date().getTime() - loopTime;
				loopTime = new Date().getTime();
			}
			//repainterRun = false;
			++level;
			//System.out.println("Averaged " + (1000 / ((new Date().getTime() - totalTime) / (countTo))) + " fps");
			actP.nextPanel();
		}
	}
	public boolean enemiesRemain() {
		synchronized (astros) {
			for(SpaceObj s : astros) {
				if(s instanceof Weapon == false)
					return true;
			}
		}
		return false;
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
	// public void populateStars(ArrayList<Star> s) {
		// int starNum = Math.min((thisSystemWidth*thisSystemHeight / 100000), 3000);
		// for(int i = 0; i < starNum; i++) {
			// s.add(new Star(thisSystemWidth, thisSystemHeight));
		// }
	// }
	// public void populateDust(ArrayList<Dust> s) {
		// int dustNum = Math.min((thisSystemWidth*thisSystemHeight / 100000), 3000);
		// for(int i = 0; i < dustNum; i++) {
			// s.add(new Dust(thisSystemWidth, thisSystemHeight));
		// }
	// }
	private /*synchronized*/ void spawnSpaceObjs() {
		//synchronized (astros) {
			astros.add(new Fighter(100, 100, getNextShipID() + level));
			astros.add(new Fighter(-100, -100, getNextShipID() + level));
			astros.add(new Fighter(-200, 200, getNextShipID() + level * 2));
			// System.out.println("6");
		//}
	}
	public int getNextShipID() {
		return shipID++;
	}
	public void fireWeapons() {
		// System.out.println("10");
		Weapon w;
		int astroSize = astros.size();
		synchronized(astros) {
			//for(SpaceObj s : astros) {
			for(int i = 0; i < astroSize; i++) {
				SpaceObj s = astros.get(i);
				if(s.selectedWeapon != -1) {
					w = getWeapon(s);				
					if(s.firing == 1) {
						// fire!
						astros.add(w);
					}
					if(s.firing > 0)
						++s.firing;
					if(s.firing > w.fireRateInverse)
						s.firing = 1;
				}
			}
			w = getWeapon(hero);
			if(hero.firing == 1) {
				// fire!
				astros.add(w);
			}
			if(hero.firing > 0)
				++hero.firing;
			if(hero.firing > w.fireRateInverse)
				hero.firing = 1;
		}
	}
	public Weapon getWeapon(SpaceObj s) {
		if(s.selectedWeapon == Weapon.LB) {
			// LaserBullet
			return new LaserBullet(s.x, s.y, s.dx, s.dy, s.maxVelocity, s.accelRate, s.angle, s.shipID);
		}
		// else if(s.selectedWeapon == ) {
		
		// }
		// else if(s.selectedWeapon == ) {
		
		// }
		// else if(s.selectedWeapon == ) {
		
		// }
		// else if(s.selectedWeapon == ) {
		
		// }
		// else if(s.selectedWeapon == ) {
		
		// } 
		return new LaserBullet(s.x, s.y, s.dx, s.dy, s.maxVelocity, s.accelRate, s.angle, s.shipID);
	}
	// When this method is all finished see if it's possible to combine
	//   all the foreach loops with astros into just one foreach loop.
	private void updatePositions() {
		// System.out.println("12");
		// the order of the following may need to change depending on when updatePositions is called
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
		
		int astroSize = astros.size();
		//synchronized (astros) {
			for(int i = 0; i < astroSize; i++) {
				SpaceObj s = astros.get(i);
				
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
		//}
		///////////////////////
		// modify velocity
		//synchronized (astros) {
			for(int i = 0; i < astroSize; i++) {
				intervalAccel(astros.get(i));
			}
		//}
		//synchronized (hero) {
			intervalAccel(hero);
		//}
		// if(hero.getdx() < 10 || hero.getdy() < 10)
			// System.out.println("hero slow");
		// The key release keyEvent sets hero.isAccel to false
		// DON'T SET hero.isAccel = false HERE !!!
		//hero.isAccel = false;
		
		
		///////////////////////
		// modify position
		
		// make sure masks and images are at the same position at the end of this method
		
		// move everything based on the current velocity of each object
		synchronized (astros) {
			for(int i = 0; i < astroSize; i++) {
				SpaceObj s = astros.get(i);
				s.x += s.dx;
				s.y += s.dy;
				if(s instanceof Weapon) {
					++((Weapon) s).cyclesLived;
					if(((Weapon) s).cyclesLived >= ((Weapon) s).timeToLive) {
						astros.remove(s);
						astroSize = astros.size();
					}
				}
			}
		}
		//synchronized (astros) {
			hero.x += hero.dx;
			hero.y += hero.dy;
		//}
		
		// move everything based on movement of centerSpaceObj
		// moveFromCenter(stars, 3);
		// moveFromCenter(dust, 1);
		// moveFromCenter(astros, 1);
		for(Star s : stars) {
			// change by movement divided by 3 so that stars move slowly and seem far away
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
		//System.out.println(stars.get(0).y);
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
		for(int i = 0; i < astroSize; i++) {
			SpaceObj s = astros.get(i);
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
		hero.x -= centerSpaceObj.dx;
		hero.y -= centerSpaceObj.dy;
		
		//System.out.println("player (" + hero.x + ", " + hero.y + ") fighter1 (" + astros.get(0).x + ", " + astros.get(0).y + ")");
	}
	public void intervalAccel(SpaceObj s) {
		//synchronized(this) {
			if(s.isAccel == 1)
				s.accelerate();
			if(s.isAccel > 0)
				++s.isAccel;
			if(s.isAccel > 4)
				s.isAccel = 1;
		//}
	}
	public void moveFromCenter(ArrayList<? extends SpaceObj> spaceObjs, int factor) {
		for(SpaceObj s : spaceObjs) {
			s.x -=  ((int) centerSpaceObj.dx) / factor;
			s.y -=  ((int) centerSpaceObj.dy) / factor;
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
	public void checkCollisions() {
		SpaceObj weaponShot, s;
		boolean hit;// = true;
		synchronized (astros) {
			for(int i = 0; i < astros.size(); i++) {
				s = astros.get(i);
				if(s.alive) {
					if(s instanceof Weapon == false) {
						//System.out.println(s.name);
						for(int j = 0; j < astros.size(); j++) {
							weaponShot = astros.get(j);
							if(weaponShot instanceof Weapon) {
								if((((Weapon)weaponShot).friendlyFire) || (weaponShot.shipID != s.shipID)) {
									// // if(bottomA <= topB)
									// if(s.y - s.diam <= weaponShot.y) 
										// hit = false; 
									// // if(topA >= bottomB)
									// if(s.y >= weaponShot.y + weaponShot.diam)
										// hit = false; 
									// // if(rightA <= leftB)
									// if(s.x + s.diam <= weaponShot.x)
										// hit = false; 
									// // if(leftA >= rightB)
									// if(s.x >= weaponShot.x + weaponShot.diam)
										// hit = false;
										
									if(s.y - s.diam / 2 <= weaponShot.y && 
										s.y + s.diam / 2 >= weaponShot.y &&
										s.x + s.diam / 2 >= weaponShot.x &&
										s.x - s.diam / 2 <= weaponShot.x) {
											hit = true;
											//System.out.println(".");
									}
									else {	
										//System.out.println(".");
										hit = false;
									}
									//hit = true;
									if(hit) {
										s.structInteg -= ((Weapon)weaponShot).damage;
										if(s.structInteg <= 0) {
											s.countDown = 170;
											s.alive = false;
											s.die();
										}
										astros.remove(weaponShot);
										--j;
									}
									//hit = true;
								}
							}
						}
					}
				}
			}
			s = hero;
			for(int j = 0; j < astros.size(); j++) {
				weaponShot = astros.get(j);
				if(weaponShot instanceof Weapon) {
					if((((Weapon)weaponShot).friendlyFire) || (weaponShot.shipID != s.shipID)) {
						// // if(bottomA <= topB)
						// if(s.y - s.diam <= weaponShot.y) 
							// hit = false; 
						// // if(topA >= bottomB)
						// if(s.y >= weaponShot.y + weaponShot.diam)
							// hit = false; 
						// // if(rightA <= leftB)
						// if(s.x + s.diam <= weaponShot.x)
							// hit = false; 
						// // if(leftA >= rightB)
						// if(s.x >= weaponShot.x + weaponShot.diam)
							// hit = false;
						//System.out.println(".");
						if(s.y - s.diam <= weaponShot.y && 
							s.y >= weaponShot.y + weaponShot.diam &&
							s.x + s.diam >= weaponShot.x &&
							s.x <= weaponShot.x + weaponShot.diam) {
								hit = true;
								//System.out.println(".");
						}
						else {
							//System.out.println(".");
							hit = false;
						}
						//hit = true;
						if(hit) {
							//System.out.println(".");
							s.structInteg -= ((Weapon)weaponShot).damage;
							if(s.structInteg <= 0) {
								s.countDown = 140;
								s.alive = false;
								s.die();
							}
							astros.remove(weaponShot);
							--j;
						}
						//hit = true;
					}
				}
			}
		}
	}
	public void kill() {
		SpaceObj s;
		synchronized (astros) {
			for(int i = 0; i < astros.size(); i++) {
				s = astros.get(i);
				if(s.countDown > 0) {
					--s.countDown;
				}
				if(s.countDown == 1) {
					astros.remove(s);
					--i;
				}
			}
		}
	}
	public class UpdatePanel extends JPanel {
		public UpdatePanel() {
			this.setMinimumSize(new Dimension(panelWidth, panelHeight));
			JButton buttonStartLevel = new JButton("Start next level");
			startLevelListener sll = new startLevelListener();
			buttonStartLevel.addActionListener(sll);
			this.add(buttonStartLevel);
		}
		protected class startLevelListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().remove(up);
				frame.repaint();
				//frame.getContentPane().add(BorderLayout.CENTER, actP);
				//frame.getContentPane().validate();  
				//frame.getContentPane().repaint();
				//actP.requestFocusInWindow();
				new Thread(new Play()).start();
			}
		}
	}
	public class ActionPanel extends JPanel {
		public ActionPanel() {
			// System.out.println("2 1/2");
			level = 1;
			//MyFocusListener mfl = new MyFocusListener();
			//this.addFocusListener(mfl);
			hero = new PlayerShip(0, 0, 1);
			//spawnSpaceObjs();
			makeCenter(hero);
			myKeyListener mkl = new myKeyListener();
			this.addKeyListener(mkl);
			this.setFocusable(true);
		}
		public void makeCenter(SpaceObj s) {
			if(centerSpaceObj != null)
				centerSpaceObj.isGameCenter = false;
			centerSpaceObj = s;
			s.isGameCenter = true;
		}
		public void paintComponent(Graphics g) {
			// System.out.println("14");
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Color.black);
			g2d.fillRect(0, 0, panelWidth, panelHeight);
			/////////////////////////
			// DRAW STARS AND DUST
			for(Star s : stars) {
				g2d.setColor(s.color);
				//g2d.fillOval(40, 40, 40, 40);
				g2d.fillOval((int) (s.x + panelWidth / 2), (int) (-(s.y) + panelHeight / 2), s.diam, s.diam);
				//System.out.println("Star at (" + (s.x + panelWidth / 2) + ", " + (-(s.y) + panelHeight / 2) + ")");
				//System.out.println("Star with diameter " + s.diam);
			}
			for(Dust s : dust) {
				g2d.setColor(s.color);
				g2d.fillOval((int) (s.x + panelWidth / 2), (int) (-(s.y) + panelHeight / 2), s.diam, s.diam);
			}
			//System.out.println(stars.size() + " stars");
			//g2d.setColor(Color.blue);
			//g2d.fillOval(40, 40, 40, 40);
			// Draw planets, space stations
			// Draw non-interactive debris
			// Draw projectiles
			/////////////////////////
			// DRAW SHIPS AND WEAPONS
			AffineTransform origXform;
			AffineTransform newXform;
			int xRot;
			int yRot;
			int frameX;
			int frameY;
			synchronized (astros) {
				SpaceObj s;
				//for(SpaceObj s : astros) {
				for(int i = 0; i < astros.size(); i++) {
					// System.out.println("frame space (" + (((int) s.x) + panelWidth / 2) + ", " + (-((int) s.y) + panelHeight / 2) + "), game space (" +
						// s.x + ", " + s.y + ")");
					s = astros.get(i);
					if(s instanceof Weapon) {
						g2d.setColor(Color.yellow);
						g2d.fillOval((int) (s.x + panelWidth / 2 - s.diam / 2), (int) (-(s.y) + panelHeight / 2 - s.diam / 2), s.diam, s.diam);
					}
					else {
						origXform = g2d.getTransform();
						newXform = (AffineTransform)(origXform.clone());
						//center of rotation is center of the panel
						xRot = ((int) s.x) + panelWidth / 2;
						yRot = -((int) s.y) + panelHeight / 2;
						newXform.rotate(-s.angle, xRot, yRot);
						g2d.setTransform(newXform);
						//draw image centered in panel
						frameX = ((int) s.x) + panelWidth / 2 - s.getImage().getWidth(this)/2;
						frameY = -((int) s.y) + panelHeight / 2 - s.getImage().getHeight(this)/2;
						g2d.drawImage(s.getImage(), frameX, frameY, this);
						g2d.setTransform(origXform);
						
						//g2d.drawImage(astros.get(i).getImage(), ((int) astros.get(i).x) + panelWidth / 2, -((int) astros.get(i).y) + panelHeight / 2, this);
					}
				}
			}
			//System.out.println("Drawing " + hero.name + " which is at (" + hero.x + ", " + hero.y + ") from center");
			//g2d.drawImage(hero.getImage(), hero.x + panelWidth / 2, -(hero.y) + panelHeight / 2, this);
			
			origXform = g2d.getTransform();
			newXform = (AffineTransform)(origXform.clone());
			//center of rotation is center of the panel
			xRot = panelWidth/2;
			yRot = panelHeight/2;
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
			// g2d.setColor(Color.black);
			// g2d.fillRect(panelWidth + 4 + mapBorder, mapBorder, mapWidth, mapWidth);
			// paint all ships, space stations, planets, and other important objects in minimap
			
			// / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /
			// All these large calculations should be stored in variables before
			//   each new level begins to save some computations.  (At least the parts of the
			//   calculations that don't depend on current position of the object.)
			// / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /
			
			synchronized (astros) {
				//for(SpaceObj s : astros) {
				for(int i = 0; i < astros.size(); i++) {
					if(astros.get(i) instanceof Weapon == false) {
						// this if statement is extra for later
						// if(s.isHostile)
						g2d.setColor(Color.red);
						// else
						// g2d.setColor(Color.green);
						g2d.fillOval(((int) (astros.get(i).x * (double) mapWidth / (double) thisSystemWidth) + (mapWidth / 2) + panelWidth + mapBorder + 4), -((int) (astros.get(i).y * (double) mapWidth / (double) thisSystemHeight)) + (mapWidth / 2) + mapBorder, 3, 3);
						//System.out.println("Drawing fighter at (" + ((int) (s.x * (double) mapWidth / (double) thisSystemWidth) + (mapWidth / 2) + panelWidth + mapBorder + 4) + ", " + (-((int) (s.y * (double) mapWidth / (double) thisSystemHeight) + (mapWidth / 2)) + mapBorder) + ") in frame space");
						//System.out.println( ((s.x * ((double) mapWidth)) / ((double) thisSystemWidth)));
						//System.out.println((s.x * ((double)mapWidth) / ((double)thisSystemWidth)));
					}
				}
			}
			g2d.setColor(Color.blue);
			//System.out.println("Drawing player at (" + ((int) (hero.x * (double) mapWidth / (double) thisSystemWidth) + (mapWidth / 2) + panelWidth + mapBorder + 4) + ", " + (-((int) (hero.y * (double) mapWidth / (double) thisSystemHeight) + (mapWidth / 2)) + mapBorder) + ") in frame space");
			g2d.fillOval((int) (hero.x * (double) mapWidth / (double) thisSystemWidth) + (mapWidth / 2) + panelWidth + mapBorder + 4, -((int) (hero.y * (double) mapWidth / (double) thisSystemHeight)) + (mapWidth / 2) + mapBorder, 3, 3);
			// paint rectangle around visible field on minimap
			g2d.setColor(new Color(0xFF00C000));
			//g2d.setColor(Color.green);
			g2d.drawRect((int) ((-panelWidth / 2) * (double) mapWidth / (double) thisSystemWidth) + (mapWidth / 2) + panelWidth + mapBorder + 4, 
				-((int) ((panelHeight / 2) * (double) mapWidth / (double) thisSystemHeight)) + (mapWidth / 2) + mapBorder, 
				(int) (panelWidth * (double) mapWidth / (double) thisSystemWidth), 
				(int) (panelHeight * (double) mapWidth / (double) thisSystemHeight));
			//g2d.flush();
		}
		public void nextPanel() {
			frame.getContentPane().remove(this);
			//frame.repaint();
			frame.getContentPane().add(BorderLayout.CENTER, up);
			frame.getContentPane().validate();  
			frame.getContentPane().repaint(); 
		}
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
				// if(e.getKeyCode() == KeyEvent.VK_DOWN)
				
				if(keyCode == KeyEvent.VK_SPACE) {
					if(hero.firing == 0)
						hero.firing = 1;
					//System.out.println("Firing");
				}
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
					System.exit(0);
				e.consume();
				//update();
				//System.out.println(e.getKeyCode() + " " + (KeyEvent.VK_RIGHT | KeyEvent.VK_LEFT));
			}
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
			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_UP) {
					//synchronized(this) {
						hero.isAccel = 0;
					//}
				} 
				// else if (keyCode == KeyEvent.VK_DOWN) {
					// down = false;
				// } 
				else if (keyCode == KeyEvent.VK_LEFT) {
					hero.turningLeft = false;
				}
				else if (keyCode == KeyEvent.VK_RIGHT) {
					hero.turningRight = false;
				}
				else if(keyCode == KeyEvent.VK_SPACE) {
					//synchronized(this) {
						hero.firing = 0;
						//System.out.println("Stopped firing");
					//}
				}
				e.consume();
				//update();
			}
		}
		public void update() {
			StringBuilder str = new StringBuilder();

			if (hero.isAccel > 0) {
				str.append(" [up]");
			}

			// if (down) {
			// if (ctrldown) {
			// str.append(" [ctrl-down]");
			// } else {
			// str.append(" [down]");
			// }
			// }

			if (hero.turningLeft) {
				str.append(" [left]");
			}

			if (hero.turningRight) {
				str.append(" [right]");
			}

			System.out.println(str.toString());
		}
		// public class MyFocusListener implements FocusListener {
			// public void focusGained(FocusEvent e) {
				// if(e.isTemporary() == false) {
					// System.out.println("Got focus");
					// play();
				// }
			// }
			// public void focusLost(FocusEvent e) {}
		// }
	}
}