import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
public class Fighter extends Ship {
    protected Fighter() {}
    public Fighter(int X, int Y, int sID, double mv) {
        // pass maxVelocity to SpaceObj
        super(mv);

        shipID = sID;

        mass = 4;

        weapons.add(BulletGun.newDefaultWeapon(this));
        structInteg = 50;

        angle = Math.random()*2*Math.PI - Math.PI;
        x = X;
        y = Y;
        dx = 0;
        dy = 0;
        baseAccelRate = .1;
        turnRate = Math.PI / 700;
        isAccel = 1;
        if (Math.random() > .5)
            turningRight = true;
        else
            turningLeft = true;
        firing = true;

        origObjImg = null;
        try {
            origObjImg = ImageIO.read(getClass().getResource("fighter.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        diam = origObjImg.getWidth();
    }
}
