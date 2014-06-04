import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.*;
public abstract class SpaceObj {
	public boolean alive;
	public int shipID;
	public double angle; // current angle in radians (East is 0 or 2*PI)
	public double x;
	public double y;
	public boolean isGameCenter;
	//public Position gamePos;
	protected double dx; // This variable is protected because we need to check if the new assigned velocity is greater than maxVelocity
	protected double dy;
	public double maxVelocity; // absolute maximum velocity
	//public double maxThrustVelocity; // maximum velocity that can be reached by thrusting directly in one direction; determined by maxVelocity
	public double accelRate; // rate of acceleration; dv/dt
	public double turnRate;
	public int isAccel; // is accelerating
	public boolean turningLeft;
	public boolean turningRight;
	public int firing;
	protected String name;
	protected BufferedImage origObjImg;
	public ArrayList<Integer> weapons;
	public int selectedWeapon; // index of selected weapon
	public int diam;
	public int structInteg; // structural integrity
	public int countDown;
	
	protected SpaceObj() {}
	public SpaceObj(double mv) {
		maxVelocity = mv;
		//maxThrustVelocity = maxVelocity * Math.cos(Math.PI / 4);
		alive = true;
		selectedWeapon = 0;
		weapons = new ArrayList<Integer>();
		countDown = 0;
	}
	public double getdx() {
		return dx;
	}
	public double getdy() {
		return dy;
	}
	// public void setdxdy(double dx2, double dy2) {
		// // double newVelocity = Math.sqrt(dx2*dx2 + dy2*dy2);
		// //System.out.println("newVelocity == " + newVelocity);
		// // if(newVelocity <= maxVelocity) {
			// // dx = dx2;
			// // dy = dy2;
		// // }
		// // else {
			// // dx = (int) (dx2 * ((double) (maxVelocity / newVelocity)));
			// // dy = (int) (dy2 * ((double) (maxVelocity / newVelocity)));
			// // // Debug messages
			// // // if(dx == 0 || dy < 0)
				// // // System.out.println("hero slow");
			// // if((int) Math.sqrt(dx*dx + dy*dy) > maxVelocity)
				// // System.out.println("ERROR: Incorrect code in SpaceObj.setdxdy()");
		// // }
		
		// if(dx2 <= maxVelocity && dx2 >= -maxVelocity)
			// dx = dx2;
		// if(dy2 <= maxVelocity && dy2 >= -maxVelocity)
			// dy = dy2;
	// }
	public void accelerate() {
		// This is wrong.  As it is, they can go faster to the NW/NE/SW/SE than they can in any other
		//   direction.  But I'm having trouble thinking of a way to do it and there are many other things
		//   that need to get done!
		double currentVelocity = Math.sqrt(dx*dx + dy*dy);
		double slowAccel = 1;
		if(currentVelocity < accelRate)
			slowAccel = .2;
		double dx2 = dx + (Math.cos(angle)*accelRate*slowAccel);
		double dy2 = dy + (Math.sin(angle)*accelRate*slowAccel);
		if(dx2 <= maxVelocity && dx2 >= -maxVelocity)
			dx = dx2;
		if(dy2 <= maxVelocity && dy2 >= -maxVelocity)
			dy = dy2;
			
		
		// double currentVelocity = Math.sqrt(dx*dx + dy*dy);
		// double newVelocity = Math.sqrt(dx2*dx2 + dy2*dy2);
		// if(newVelocity <= maxVelocity) {
			// dx = dx2;
			// dy = dy2;
		// }
		
		// the purpose of this part is to make it possible to come to a complete stop.
	// The idea is, if the player achieves a VERY slow velocity by thrusting in the direction
	//   opposite to their inertia, they must be trying to stop.
	// Trouble is, if their ship's acceleration is slow enough, this might prevent them from moving
	//   at all, or at least moving in certain directions, especially if they're at rest.
	
	// I subtracted .01 because I'm worried the round off error might make it possible to stop
	//   at unwanted times, like when you're just barely going too slow.
	currentVelocity = Math.sqrt(dx*dx + dy*dy);
	if(currentVelocity < accelRate / 10)
		dx = dy = 0;	
	}
	public void checkHeightWidth() {
		if(origObjImg.getHeight() != origObjImg.getWidth()) {
			System.out.println(name + "'s image is not square");
		}
	}
	public BufferedImage getImage() {
		return origObjImg;
	}
	public void die() {
		isAccel = 0;
		turningRight = false;
		turningLeft = false;
		firing = 0;
		try {
			origObjImg = ImageIO.read(getClass().getResource("ballofflame.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}