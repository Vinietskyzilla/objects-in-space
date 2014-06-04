import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
public abstract class SpaceObj {
	public boolean alive;
	public double angle; // current angle in radians (East is 0 or 2*PI)
	public int x;
	public int y;
	public boolean isGameCenter;
	//public Position gamePos;
	protected double dx; // This variable is protected because we need to check if the new assigned velocity is greater than maxVelocity
	protected double dy;
	public double maxVelocity;
	public double accelRate; // rate of acceleration; dv/dt
	public double turnRate;
	public boolean isAccel; // is accelerating
	public boolean turningLeft;
	public boolean turningRight;
	public boolean firing;
	protected String name;
	BufferedImage origObjImg;
	public SpaceObj() {
		alive = true;
	}
	public double getdx() {
		return dx;
	}
	public double getdy() {
		return dy;
	}
	public void setdxdy(double dx2, double dy2) {
		double newVelocity = Math.sqrt(dx2*dx2 + dy2*dy2);
		//System.out.println("newVelocity == " + newVelocity);
		if(newVelocity <= maxVelocity) {
			dx = dx2;
			dy = dy2;
		}
		// else {
			// dx = (int) (dx2 * ((double) (maxVelocity / newVelocity)));
			// dy = (int) (dy2 * ((double) (maxVelocity / newVelocity)));
			// // Debug messages
			// // if(dx == 0 || dy < 0)
				// // System.out.println("hero slow");
			// if((int) Math.sqrt(dx*dx + dy*dy) > maxVelocity)
				// System.out.println("ERROR: Incorrect code in SpaceObj.setdxdy()");
		// }
	}
	public void checkHeightWidth() {
		if(origObjImg.getHeight() != origObjImg.getWidth()) {
			System.out.println(name + "'s image is not square");
		}
	}
	public BufferedImage getImage() {
		return origObjImg;
	}
}