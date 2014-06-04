// ¤ • · ° * + ¤ · ° * • ' . · ° * ` + ¤ •
//	° *	  ·   Objects in°Space    *     '
//   ¤    ·   *     °   '   *    `     ¤
//	·   ° *   By David·Winiecki  ¤  •   ·
// ° `   ¤  •  *January 2011 ·    °   *   
//   ¤   ·   °   •   ·  °  `   ¤  °  *  `
// * • ' . · ° * ` + ¤ • ¤ • · ° * + ¤ · °


// REMEMBER: I want everything to center on the player's ship.  Their ship
//    will never move.  Instead, when they accelerate, the things around
//    them will be moved accordingly.  (Just like Futurama.)  :)

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
public class Game {
	JFrame frame;
	int width;  // desired width
	int height; // desired height
	UpdatePanel up;
	ActionPanel actP;
	PlayerShip hero;
	ArrayList<SpaceObj> astros = new ArrayList<SpaceObj>();
	public static void main(String[] args) {
		// Game g = new Game();
		// g.run();
		new Game().run();
	}
	public Game() {
		// initialize GUI
		// initialize music
		// initialize sound
		// initialize input
		width = 700;
		height = 700;
		int horizantalWindowBorderOffset = 6;
		int headerWindowBorderOffset = 33;
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//FlowLayout fl = new FlowLayout();
		//frame.setLayout(fl);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds((dim.width / 2) - (width / 2) - (horizantalWindowBorderOffset / 2),
			(dim.height / 2) - (height / 2) - (headerWindowBorderOffset - 3),
			width + horizantalWindowBorderOffset,
			height + headerWindowBorderOffset);
		//frame.setBounds(0, 0, width, height);
		frame.setResizable(false);
		up = new UpdatePanel();
		actP = new ActionPanel();
		// other menu and game panels here
		frame.getContentPane().add(BorderLayout.CENTER, up);
		//frame.pack();
	}
	public void run() {
		frame.setVisible(true);
		// up is in focus, player may click on buttons in that panel causing further events
	}
	
	public void play() {
		// gameDim.width = ;
		// gameDim.height = ;
		//hero.gamePos.x = gameDim.width / 2;
		//hero.gamePos.y = gameDim.height / 2;
		//spawnSpaceObjs();
		for(SpaceObj s : astros) {
			System.out.println("Found an object in astros");
		}
		// debug code
		for(SpaceObj s : astros) {
			s.checkHeightWidth();
		}
		hero.checkHeightWidth();
		boolean levelComplete = false;
		int tempVar = 0;
		int slowCtr = 0;
		long totalTime = new Date().getTime();
		while(levelComplete == false) {
			long msStart = new Date().getTime();
			updatePositions();
			//collisions();
			//possibleSpawn()
			actP.validate();
			//actP.revalidate();
			//actP.repaint();
			//frame.getContentPane().repaint(); 
			++hero.x;
			try {
				// Make sure that the game runs at the same speed on all systems
				msStart = 16 - (new Date().getTime() - msStart);
				if(msStart > 0)
					Thread.sleep(msStart);
				else
					++slowCtr;
			} catch (Exception e) {
				e.printStackTrace();
			}
			++tempVar;
			if(tempVar == 180)
				levelComplete = true;
		}
		++actP.level;
		astros.clear();
		spawnSpaceObjs();
		if(slowCtr > 0)
			System.out.println(slowCtr + " loops ran at less than 60 fps");
		//System.out.println(new Date().getTime() + " " + totalTime + " " + (new Date().getTime() - totalTime));
		System.out.println("Averaged " + (1000 / ((new Date().getTime() - totalTime) / 180.0)) + " fps");
		actP.nextPanel();
	}
	private void spawnSpaceObjs() {
		//if(level == 1)
			// spawn easy stuff
		//if(level % 3 == 0)
			// add the next thing to the list of possible ships
			
		astros.add(new Fighter(100, 100));
		astros.add(new Fighter(-100, -100));
	}
	private void possibleSpawn() {
		//if(a bunch of time has passed or something)
			//spawnSpaceObjs();
	}
	private void updatePositions() {
		// the order of the following may need to change depending on when updatePositions is called
		// modify acceleration
		// modify velocity
		// modify position
		
		// make sure masks and images are at the same position at the end of this method
	}
	private void collisions() {
		// make sure masks and images are at the same position when checking collisions
	}
		
	public class UpdatePanel extends JPanel {
		public UpdatePanel() {
			this.setMinimumSize(new Dimension(width, height));
			JButton buttonStartLevel = new JButton("Start next level");
			startLevelListener sll = new startLevelListener();
			buttonStartLevel.addActionListener(sll);
			this.add(buttonStartLevel);
		}
		protected class startLevelListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().remove(up);
				frame.repaint();
				frame.getContentPane().add(BorderLayout.CENTER, actP);
				//frame.repaint();
				frame.getContentPane().validate();  
				frame.getContentPane().repaint();
				actP.requestFocusInWindow();
			}
		}
	}
	public class ActionPanel extends JPanel {
		//ArrayList<SpaceObj> astros = new ArrayList<SpaceObj>(); // all the SpaceObjs this round
		// Not sure I'll include this object.  Should really use an enum.
		//ArrayList<String> astroNames = new ArrayList<String>(); // allowed SpaceObjs
		int level;
		//Dimension gameDim; // dimensions of the game field (usually much larger than the window size)
		//Position gameCenter; // location on the game field to center the ActionPanel (you could also think of it as where to point the camera)
		public ActionPanel() {
			level = 1;
			MyFocusListener mfl = new MyFocusListener();
			this.addFocusListener(mfl);
			//gameDim = new Dimension(2000, 2000);
			hero = new PlayerShip(0, 0);
			spawnSpaceObjs();
		}
		// public void play() {
			// // gameDim.width = ;
			// // gameDim.height = ;
			// //hero.gamePos.x = gameDim.width / 2;
			// //hero.gamePos.y = gameDim.height / 2;
			// spawnSpaceObjs();
			// for(SpaceObj s : astros) {
				// System.out.println("Found an object in astros");
			// }
			// // debug code
			// for(SpaceObj s : astros) {
				// s.checkHeightWidth();
			// }
			// hero.checkHeightWidth();
			// boolean levelComplete = false;
			// int tempVar = 0;
			// int slowCtr = 0;
			// long totalTime = new Date().getTime();
			// while(levelComplete == false) {
				// long msStart = new Date().getTime();
				// updatePositions();
				// //collisions();
				// //possibleSpawn();
				// this.repaint();
				// ++hero.gamePos.x;
				// try {
					// // Make sure that the game runs at the same speed on all systems
					// msStart = 16 - (new Date().getTime() - msStart);
					// if(msStart > 0)
						// Thread.sleep(msStart);
					// else
						// ++slowCtr;
				// } catch (Exception e) {
					// e.printStackTrace();
				// }
				// ++tempVar;
				// if(tempVar == 180)
					// levelComplete = true;
			// }
			// ++level;
			// //astros.clear();
			// if(slowCtr > 0)
				// System.out.println(slowCtr + " loops ran at less than 60 fps");
			// //System.out.println(new Date().getTime() + " " + totalTime + " " + (new Date().getTime() - totalTime));
			// System.out.println("Averaged " + (1000 / ((new Date().getTime() - totalTime) / 180.0)) + " fps");
			// nextPanel();
		// }
		// private void spawnSpaceObjs() {
			// //if(level == 1)
				// // spawn easy stuff
			// //if(level % 3 == 0)
				// // add the next thing to the list of possible ships
				
			// astros.add(new Fighter(new Position(100, 100)));
			// astros.add(new Fighter(new Position(-100, -100)));
		// }
		// private void possibleSpawn() {
			// //if(a bunch of time has passed or something)
				// //spawnSpaceObjs();
		// }
		// private void updatePositions() {
			// // the order of the following may need to change depending on when updatePositions is called
			// // modify acceleration
			// // modify velocity
			// // modify position
			
			// // make sure masks and images are at the same position at the end of this method
		// }
		// private void collisions() {
			// // make sure masks and images are at the same position when checking collisions
		// }
		// called by frame.repaint()
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Color.black);
			g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
			//g2d.setColor(Color.green);
			//g2d.fillRect(398, 398, 1, 1);
			//g2d.setPaint(gradient);
			drawEverything(g2d);
			//g2d.drawImage(origShipImg, 50, 50, this);
		}
		// I'm sure there are some changes I could make to make this more efficient
		public void drawEverything(Graphics2D g) {
			//findCenter();
			System.out.println("size of astros " + astros.size());
			for(SpaceObj s : astros) {
				//p = s.gamePos.getPositionInt();
				System.out.println("Drawing " + s.name + " which is at (" + s.x + ", " + s.y + ") from center");
				g.drawImage(s.getImage(), (int) s.x + width / 2, (int) -(s.y) + height / 2, this);
			}
			//p = hero.gamePos.getPositionInt();
			System.out.println("Drawing " + hero.name + " which is at (" + hero.x + ", " + hero.y + ") from center");
			g.drawImage(hero.getImage(), (int) hero.x + width / 2, (int) -(hero.y) + height / 2, this);
		}
		public void findCenter() {
			//gameCenter = hero.gamePos;
		}		
		// public Position flip(Position yup) {
			// return new Position(yup.x, height - yup.y);
		// }
		public void nextPanel() {
			frame.getContentPane().remove(this);
			frame.repaint();
			frame.getContentPane().add(BorderLayout.CENTER, up);
			//frame.pack();
			//frame.repaint();
			frame.getContentPane().validate();  
			frame.getContentPane().repaint(); 
		}
		public class MyFocusListener implements FocusListener {
			public void focusGained(FocusEvent e) {
				if(e.isTemporary() == false) {
					//System.out.println("Got focus");
					//frame.getContentPane().add(BorderLayout.CENTER, this);
					// frame.getContentPane().validate();  
					// frame.getContentPane().repaint();
					play();
				}
			}
			public void focusLost(FocusEvent e) {}
		}
	}
}	
	
	
	
	
	//////////////////////////////////
	//
	// Itty bitty goals:
	//
	// (They're not really itty bitty at all, but relative to doing EVERYTHING, they are quite a bit smaller.)
	//
	// Start on the upgrades panel, music plays, m turns music on and off.
	// The upgrades panel just uses regular JLabels and JTextBoxes, and looks ugly.
	// Upon clicking start next level, next level starts.
	// The action game panel should be just as good in this version as in the big goals version.
	// When all enemies are destroyed, go back to upgrades page.
	//
	// Consider using integers or longs instead of doubles for representing position.
	//
	//////////////////////////////////
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//																														//
	//																														//
	//		BEGIN, BIG GIANT LONG TERM GOALS:																				//
	//																														//
	//																														//
	//																														//
																															//
																															//
																															//
																															//
																															//
	
	// Actually, FORGET THE MENU BAR.  Do everything in overlay menus.
	
	// Game contains gui frame
	// frame contains menu bar* at top and current game panel in center
	// Game switches to a different game panel from the menu bar* AND from
	//   menu panels that appear and overlay game panels
	// Game sends a pause command to the game panels (either all of the game
	//   panels or the currently active panel) using method calls to the game
	//   panel objects.
	// The exit button on the window should cause a prompt asking if you want to quit
	//   without saving, and handle it in a clear and intuitive way.
	
	// No panels will be translucent.  Menus will be smaller than the game panels.
	//   Menus will be dark with light (but not bright) text and button rectangles.
	// All menu panels will be the same size.
	// The action game panel will be as large as the playing area.  If the window is
	//   smaller than this, the visible action game panel will center on the player's ship.
	
	// Resizing the window will always pause, unless the user selects an option that
	//   turns this off.
	// There will be no maximum window size.  The game panel will be placed in the center
	//   with a black border if there is extra space.
	
	// All game panels will respond to esc by dimming the game panel and opening
	//   the file menu
	// Selecting any part of the menu bar* with the mouse will automatically pause.
	// The action game panel will respond to arrow keys, a few keys for weapons
	//   and other powers, and p for pause
	// The upgrade game panel will respond to arrow keys and enter, which will change
	//   which "buttons" are highlighted.  There will be a back button on appropriate
	//   menu panels.  There will not be a save changes button.  All changes will take
	//   effect immediately.
	
	// When m is pushed to turn music on/off, music should either be muted until the end of the song,
	//   and then pause at the beginning of the next song, or music should be paused
	
	// 1. Menu bar* contains "Start Menu", "Options", and "Quit".  When playing, a "Save" option will
	//      appear.
	// 2. Start menu (a game panel) displays with "buttons" for new game, load, options, and exit
	// 3. If they select new game, a game panel with instructions will appear, containing a
	//      "continue" button and a place to enter their name.  Then the upgrade game panel will appear.
	//    If they select options or load, a black game panel will appear overlaid by the options menu
	//      or the load menu (both are menu panels).
	// 4. The upgrade game panel will list what's available, what the player has, and how much of each.
	//    The player can save from the menu bar* or from the menu panel that appears when they press esc.
	//    If they press esc, a modified options menu will overlay the upgrade game panel that
	//      contains a save option and any other changes that make sense (like taking out options that
	//      shouldn't or can't be changed while playing the game.)
	//    If the player saves halfway through picking what they want, the save file will include their
	//      partial choices.
	//    The player can take back money and other choices made during this upgrade phase with no penalty.
	//      This will need to be included in the save file as well.
	//    When the player is finished, there will be a begin next level button, and upon selecting it, they
	//      will be prompted if they are ready to continue.
	// 5. The load menu will need multiple panels or a scroll bar box that lengthens as they save more files.
	//    I'm leaning toward not limiting how many saves they can make.  There should be load, delete, and back
	//      buttons.
	// 6. 
	
	// * Actually, FORGET THE MENU BAR.  Do everything in overlay menus.
	
	// All options in the options menus should be saved in a single file used for all players
	
	// Options in the main options menu:
	
	// pause on window resize
	// full screen mode
	// Relist the hotkeys (p:pause, esc:options, m:music, s:sound effects)
	// Allow them to change the keyboard layout (any and all keys, even esc; also allow them to chain shift, ctrl, and alt)
	// Allow them to save option settings in a file
	// Allow them to reset everything to defaults.
	// Allow them to reset any individual thing to its default.
	
	
	// Modifications for the in-game options menu:   (it will be slightly different than the options menu that appears from all other panels)
	
	// save option  (I think I want this during the upgrades phase instead or as well)
	
	
	// Extras
	
	// Make an invisible observer ship that becomes controllable when paused, so the player can look around
	// A stats page would be cool.
	// Sounds that originate further away should be quieter.
	// If a sound originates from the left, the left speaker should produce more of the sound than the right speaker, and
	//   vice versa.  This should not be noticeable for sounds that originate nearby, and should be most noticeable at
	//   the greatest distance possible, but it shouldn't stand out.
	// Make the music cycle through whatever song files are in a certain folder, regardless of the songs' names.
	// Make keyboard buttons for pause music, next song, and last song.
	// Add options to turn on/off all or particular sound effects.
	
	// * Actually, FORGET THE MENU BAR.  Do everything in overlay menus.

	
																															//	
																															//	
																															//	
																															//
	//																														//
	//																														//
	//		END, BIG GIANT LONG TERM GOALS:																					//
	//																														//
	//																														//
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	