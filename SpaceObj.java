import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.*;
public abstract class SpaceObj {
    public int shipID;
    public String name;
    // Structural integrity.
    public int structInteg;
    // Current angle in radians. (East is 0 or 2*PI.)
    public double angle;
    public double x;
    public double y;
    // This variable is protected because we need to check if the new assigned
    // velocity is greater than maxVelocity.
    protected double dx;
    protected double dy;
    // Absolute maximum velocity.
    public double maxVelocity;
    // Rate of acceleration or dv/dt.
    public double baseAccelRate;
    public double turnRate;
    // Is accelerating.
    public int isAccel;
    public boolean isBoosting;
    public boolean turningLeft;
    public boolean turningRight;
    public int firing;
    public BufferedImage origObjImg;
    public int diam;

    protected SpaceObj() {}
    public SpaceObj(double mv) {
        maxVelocity = mv;
        isBoosting = false;
    }
    public void accelerate() {
        
        double accelRate = baseAccelRate;
        double currentMagnitude = Math.sqrt(dx*dx + dy*dy);

        if (isBoosting) {
            accelRate *= 2;
        } else if (this instanceof Ship && currentMagnitude < accelRate) {
            // If currently travelling slowly, then accelerate slowly.
            accelRate *= .2;
        }

        // Determining new velocity: Take the vector representing the current
        // velocity and the vector representing the addition of this increment
        // of acceleration, and add them together. If the magnitude of this
        // new vector is less than or equal to maxVelocity, we're done. If it's
        // greater, then we need to find the vector with length maxVelocity and
        // with direction of the sum vector. (Another way to visualize this is
        // where the sum vector intersects a circle with radius maxVelocity.)

        // Take the vector representing the current velocity...
        double startDX = dx,
               startDY = dy;
        // and the vector representing the addition of this increment of
        // acceleration...
        double accelDX = Math.cos(angle) * accelRate,
               accelDY = Math.sin(angle) * accelRate;
        // and add them together.
        dx += accelDX;
        dy += accelDY;
        // If the magnitude of this new vector...
        double newMagnitude = Math.sqrt(dx*dx + dy*dy);
        // is less than or equal to maxVelocity, we're done. If it's greater...
        if ((newMagnitude > maxVelocity && !isBoosting)
            || newMagnitude > maxVelocity * 2) {
            // then we need to find the vector with length maxVelocity and with
            // direction of the sum vector.
            double theta = Math.atan(dy/dx);
            if (dx < 0)
              theta += Math.PI;
            dx = Math.cos(theta) * maxVelocity;
            dy = Math.sin(theta) * maxVelocity;
        }
    }
    public void die() {
        isAccel = 0;
        turningRight = false;
        turningLeft = false;
        firing = 0;
        double xDirection = 0;
        if(dx < 0)
            xDirection = Math.PI;
        // If the ship is not motionless, which would screw up the angle
        // calculation, then calculate the new angle.
        if(dx != 0 || dy != 0)
            angle = Math.atan(dy/dx) + xDirection;
        try {
            origObjImg =
              ImageIO.read(getClass().getResource("ballofflame.png"));
        } catch (IOException e) { e.printStackTrace(); }
    }
}
