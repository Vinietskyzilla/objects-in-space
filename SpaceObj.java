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
    //public Position gamePos;
    protected double dx; // This variable is protected because we need to check if the new assigned velocity is greater than maxVelocity
    protected double dy;
    public double maxVelocity; // absolute maximum velocity
    public double accelRate; // rate of acceleration; dv/dt
    public double turnRate;
    public int isAccel; // is accelerating
    public boolean isBoosting;
    public boolean turningLeft;
    public boolean turningRight;
    public int firing;
    protected String name;
    protected BufferedImage origObjImg;
    public int diam;
    public int structInteg; // structural integrity
    public int countDown;

    protected SpaceObj() {}
    public SpaceObj(double mv) {
        maxVelocity = mv;
        isBoosting = false;
        alive = true;
        countDown = 0;
    }
    public double getdx() {
        return dx;
    }
    public double getdy() {
        return dy;
    }
    public void accelerate() {
        // This is wrong. As it is, they can go faster to the NW/NE/SW/SE than they can in any other
        //     direction. But I'm having trouble thinking of a way to do it and there are many other things
        //     that need to get done!
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

    // the purpose of this part is to make it possible to come to a complete stop.
    // The idea is, if the player achieves a VERY slow velocity by thrusting in the direction
    //     opposite to their inertia, they must be trying to stop.
    // Trouble is, if their ship's acceleration is slow enough, this might prevent them from moving
    //     at all, or at least moving in certain directions, especially if they're at rest.
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
        double xDirection = 0;
        if(dx < 0)
            xDirection = Math.PI;
        // if the ship is not motionless, which would screw up the angle calculation
        if(dx != 0 || dy != 0)
            angle = Math.atan(dy/dx) + xDirection;
        try {
            origObjImg = ImageIO.read(getClass().getResource("ballofflame.png"));
        } catch (IOException e) { e.printStackTrace(); }
    }
}
