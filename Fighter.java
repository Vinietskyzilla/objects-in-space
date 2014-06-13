import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
public class Fighter extends Ship {
    protected Fighter() {}
    public Fighter(int X, int Y, int sID, double mv) {
        // pass maxVelocity to SpaceObj
        super(mv);

        name = "Fighter";
        shipID = sID;

        mass = 4;

        weapons.add(Weapon.LB);
        structInteg = 50;

        angle = Math.random()*2*Math.PI;
        x = X;
        y = Y;
        dx = 0;
        dy = 0; // y increases UPWARD
        baseAccelRate = .06;
        turnRate = Math.random()*Math.PI / (1200) + Math.PI / (1200);
        isAccel = 1;
        if(Math.random() > .5)
            turningRight = true;
        else
            turningLeft = true;
        firing = 1;

        origObjImg = null;
        try {
            origObjImg = ImageIO.read(getClass().getResource("fighter.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        diam = origObjImg.getWidth();
    }
}
