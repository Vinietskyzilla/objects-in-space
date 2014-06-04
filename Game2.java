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
public class Game2 {
	JFrame frame;
	int panelWidth;  // desired width for the square in which you fly
	int panelHeight; // desired height for the square in which you fly
	int sideMenuWidth;
	UpdatePanel up;
	ActionPanel actP;
	PlayerShip hero;
	ArrayList<SpaceObj> astros = new ArrayList<SpaceObj>();
	ArrayList<Star> stars = new ArrayList<Star>();
	ArrayList<Dust> dust = new ArrayList<Dust>();
	int level;
	SpaceObj centerSpaceObj;
	//boolean repainterRun;
	public static void main(String[] args) {
		new Game2();
	}
	public Game2() {
		int horizantalWindowBorderOffset = 6;
		int headerWindowBorderOffset = 28;
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		sideMenuWidth = 300;
		
		/////////////////////////////////////////////////////////////////////////////////////////////
		//
		// BEGIN: THIS STUFF NEEDS SERIOUS TESTING
		
		// full screen
		panelWidth = dim.width - sideMenuWidth;
		panelHeight = dim.height;
		
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
		frame.setVisible(true);
	}
	public class Play implements Runnable {
		public void run() {
			int thisSystemWidth = 60000;
			int thisSystemHeight = 60000;
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
			astros.clear();
			spawnSpaceObjs();
			boolean levelComplete = false;
			int tempVar = 0;
			hero.turningRight = false;
			hero.turningLeft = false;
			hero.isAccel = false;
			hero.dx = 0;
			hero.dy = 0;
			hero.angle = .5*Math.PI;
			// frame.getContentPane().add(BorderLayout.CENTER, actP);
			frame.getContentPane().add(actP);
			frame.getContentPane().validate();
			frame.getContentPane().repaint();
			actP.requestFocus();
			//repainterRun = true;
			//Thread repainterThread = new Thread(new Repainter());
			//repainterThread.start();
			long totalTime = new Date().getTime();
			while(levelComplete == false) {
				long msStart = new Date().getTime();
				updatePositions(thisSystemWidth, thisSystemHeight);
				actP.repaint();
				try {
					// Make sure that the game runs at the same speed on all systems
					msStart = 16 - (new Date().getTime() - msStart);
					if(msStart > 0)
						Thread.sleep(msStart);
				} catch (Exception e) {
					e.printStackTrace();
				}
				++tempVar;
				if(tempVar == 900)
					levelComplete = true;
			}
			//repainterRun = false;
			++level;
			System.out.println("Averaged " + (1000 / ((new Date().getTime() - totalTime) / (900.0))) + " fps");
			actP.nextPanel();
		}
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
	// public void populateStars(ArrayList<Star> s, int systemWidth, int systemHeight) {
		// int starNum = Math.min((systemWidth*systemHeight / 100000), 3000);
		// for(int i = 0; i < starNum; i++) {
			// s.add(new Star(systemWidth, systemHeight));
		// }
	// }
	// public void populateDust(ArrayList<Dust> s, int systemWidth, int systemHeight) {
		// int dustNum = Math.min((systemWidth*systemHeight / 100000), 3000);
		// for(int i = 0; i < dustNum; i++) {
			// s.add(new Dust(systemWidth, systemHeight));
		// }
	// }
	private void spawnSpaceObjs() {
		astros.add(new Fighter(100, 100));
		astros.add(new Fighter(-100, -100));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// When this method is all finished see if it's possible to combine
	//   all the foreach loops with astros into just one foreach loop.
	private void updatePositions(int systemWidth, int systemHeight) {
		// the order of the following may need to change depending on when updatePositions is called
		// modify rotation
		if(hero.turningLeft) {
			hero.angle += hero.turnRate;
			if(hero.angle > 2*Math.PI)
				hero.angle -= 2*Math.PI;
		}
		//hero.turningLeft = false;
		if(hero.turningRight) {
			hero.angle -= hero.turnRate;
			if(hero.angle < 0)
				hero.angle += 2*Math.PI;
		}
		//hero.turningRight = false;
		// modify acceleration
		for(SpaceObj s : astros) {
			if(s.isAccel) {
				s.setdxdy(s.getdx() + (Math.cos(s.angle)*s.accelRate),
					s.getdy() + (Math.sin(s.angle)*s.accelRate));
				// s.accelerate();
			}
			s.isAccel = false;
		}
		if(hero.isAccel) {
			//System.out.println("hero.isAccel == true");
			//System.out.println("Accelerating on x at " + ((int) (Math.cos(hero.angle)*hero.accelRate)));
			hero.setdxdy(hero.getdx() + (Math.cos(hero.angle)*hero.accelRate),
				hero.getdy() + (Math.sin(hero.angle)*hero.accelRate));
			// hero.accelerate();
		}
		// if(hero.getdx() < 10 || hero.getdy() < 10)
			// System.out.println("hero slow");
		// The key release keyEvent sets hero.isAccel to false
		// DON'T SET hero.isAccel = false HERE !!!
		//hero.isAccel = false;
		
		// modify velocity
		// modify position
		
		// make sure masks and images are at the same position at the end of this method
		
		// move everything based on the current velocity of each object
		for(SpaceObj s : astros) {
			s.x += s.dx;
			s.y += s.dy;
		}
		hero.x += hero.dx;
		hero.y += hero.dy;
		
		// move everything based on movement of centerSpaceObj
		// moveFromCenter(stars, 3, systemWidth, systemHeight);
		// moveFromCenter(dust, 1, systemWidth, systemHeight);
		// moveFromCenter(astros, 1, systemWidth, systemHeight);
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
		for(SpaceObj s : astros) {
			s.x -= ((int) centerSpaceObj.dx);
			s.y -= ((int) centerSpaceObj.dy);
			if(s.x < -systemWidth/2)
				s.x += systemWidth;
			else if(s.x > systemWidth/2)
				s.x -= systemWidth;
			if(s.y < -systemHeight/2)
				s.y += systemHeight;
			else if(s.y > systemHeight/2)
				s.y -= systemHeight;
		}
		hero.x -= centerSpaceObj.dx;
		hero.y -= centerSpaceObj.dy;
		
		//System.out.println("player (" + hero.x + ", " + hero.y + ") fighter1 (" + astros.get(0).x + ", " + astros.get(0).y + ")");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void moveFromCenter(ArrayList<? extends SpaceObj> spaceObjs, int factor, int systemWidth, int systemHeight) {
		for(SpaceObj s : spaceObjs) {
			s.x -=  ((int) centerSpaceObj.dx) / factor;
			s.y -=  ((int) centerSpaceObj.dy) / factor;
			if(s.x < -systemWidth/2)
				s.x += systemWidth;
			else if(s.x > systemWidth/2)
				s.x -= systemWidth;
			if(s.y < -systemHeight/2)
				s.y += systemHeight;
			else if(s.y > systemHeight/2)
				s.y -= systemHeight;
		}
	}
	// public class Repainter implements Runnable {
		// public void run() {
			// while(repainterRun) {
				// actP.repaint();
				// long msStart = new Date().getTime();
				// try {
					// // Make sure that the game runs at the same speed on all systems
					// msStart = 16 - (new Date().getTime() - msStart);
					// if(msStart > 0)
						// Thread.sleep(msStart);
				// } catch (Exception e) {
					// e.printStackTrace();
				// }
			// }
		// }
	// }
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
			level = 1;
			//MyFocusListener mfl = new MyFocusListener();
			//this.addFocusListener(mfl);
			hero = new PlayerShip(0, 0);
			spawnSpaceObjs();
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
		public synchronized void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Color.black);
			g2d.fillRect(0, 0, panelWidth, panelHeight);
			// Draw stars
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
			// Draw ships
			for(SpaceObj s : astros) {
				//System.out.println("Drawing " + s.name + " which is at (" + s.x + ", " + s.y + ") from center");
				g2d.drawImage(s.getImage(), ((int) s.x) + panelWidth / 2, -((int) s.y) + panelHeight / 2, this);
			}
			//System.out.println("Drawing " + hero.name + " which is at (" + hero.x + ", " + hero.y + ") from center");
			//g2d.drawImage(hero.getImage(), hero.x + panelWidth / 2, -(hero.y) + panelHeight / 2, this);
			
			AffineTransform origXform = g2d.getTransform();
			AffineTransform newXform = (AffineTransform)(origXform.clone());
			//center of rotation is center of the panel
			int xRot = panelWidth/2;
			int yRot = panelHeight/2;
			newXform.rotate(-hero.angle, xRot, yRot);
			g2d.setTransform(newXform);
			//draw image centered in panel
			int x = ((int) hero.x) + panelWidth / 2 - hero.getImage().getWidth(this)/2;
			int y = -((int) hero.y) + panelHeight / 2 - hero.getImage().getHeight(this)/2;
			g2d.drawImage(hero.getImage(), x, y, this);
			g2d.setTransform(origXform);
			
			// paint side bar
			g2d.setColor(Color.black);
			g2d.fillRect(panelWidth, 0, sideMenuWidth, panelHeight);
			g2d.setColor(new Color(0xFF808080));
			g2d.fillRect(panelWidth, 0, 4, panelHeight);
			
			// paint minimap
			
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
					hero.isAccel = true;
				// if(e.getKeyCode() == KeyEvent.VK_DOWN)
				
				if(keyCode == KeyEvent.VK_SPACE)
					hero.firing = true;
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
					hero.isAccel = false;
				} 
				// else if (keyCode == KeyEvent.VK_DOWN) {
					// down = false;
				// } 
				else if (keyCode == KeyEvent.VK_LEFT) {
					hero.turningLeft = false;
				} else if (keyCode == KeyEvent.VK_RIGHT) {
					hero.turningRight = false;
				}
				e.consume();
				//update();
			}
		}
		public void update() {
			StringBuilder str = new StringBuilder();

			if (hero.isAccel) {
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