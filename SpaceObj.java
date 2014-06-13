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
    public double accelRate;
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
    public double getdx() {
        return dx;
    }
    public double getdy() {
        return dy;
    }
    public void accelerate() {
        // This is wrong. As it is, they can go faster to the NW/NE/SW/SE than
        // they can in any other direction.
        double currentVelocity = Math.sqrt(dx*dx + dy*dy);
        double slowAccel = 1;
        if(currentVelocity < accelRate)
            slowAccel = .2;
        double dx2, dy2;
        if (isBoosting) {
            dx2 = dx + 2*(Math.cos(angle)*accelRate*slowAccel);
            dy2 = dy + 2*(Math.sin(angle)*accelRate*slowAccel);
            if(dx2 <= 2*maxVelocity && dx2 >= -2*maxVelocity)
                dx = dx2;
            if(dy2 <= 2*maxVelocity && dy2 >= -2*maxVelocity)
                dy = dy2;
        } else {
            dx2 = Math.cos(angle)*accelRate*slowAccel;
            dy2 = Math.sin(angle)*accelRate*slowAccel;
            if((dx + dx2 <= maxVelocity && dx + dx2 >= -maxVelocity) ||
                (dx2 < 0 && dx > 0) || (dx2 > 0 && dx < 0))
                dx = dx + dx2;
            if((dy + dy2 <= maxVelocity && dy + dy2 >= -maxVelocity) ||
                (dy2 < 0 && dy > 0) || (dy2 > 0 && dy < 0))
                dy = dy + dy2;
        }

        // double currentVelocity = Math.sqrt(dx*dx + dy*dy);
        // double newVelocity = Math.sqrt(dx2*dx2 + dy2*dy2);
        // if(newVelocity <= maxVelocity) {
            // dx = dx2;
            // dy = dy2;
        // }

    // The purpose of this part is to make it possible to come to a complete
    // stop. The idea is, if the player achieves a VERY slow velocity by
    // thrusting in the direction opposite to their inertia, they must be
    // trying to stop. Trouble is, if their ship's acceleration is slow enough,
    // this might prevent them from moving at all, or at least moving in
    // certain directions, especially if they're at rest.
    currentVelocity = Math.sqrt(dx*dx + dy*dy);
    if(currentVelocity < accelRate / 10)
        dx = dy = 0;
    }
    public void checkHeightWidth() {
        if(origObjImg.getHeight() != origObjImg.getWidth()) {
            System.out.println(name + "'s image is not square");
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
        // calculation, then...
        if(dx != 0 || dy != 0)
            angle = Math.atan(dy/dx) + xDirection;
        try {
            origObjImg =
              ImageIO.read(getClass().getResource("ballofflame.png"));
        } catch (IOException e) { e.printStackTrace(); }
    }
}
