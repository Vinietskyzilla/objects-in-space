import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
public class PlayerShip extends Ship {
    int structIntegInit;
    boolean playerIsAccel;
    protected PlayerShip() {}
    public PlayerShip(int X, int Y, int sID) {
        // Pass maxVelocity to SpaceObj.
        super(5);

        name = "PlayerShip";
        shipID = sID;

        weapons.add(Weapon.LB);
        structIntegInit = 150;
        structInteg = structIntegInit;

        angle = .5*Math.PI;
        x = X;
        y = Y;
        dx = 0;
        // y increases UPWARD.
        dy = 0;
        baseAccelRate = .2;
        playerIsAccel = false;
        turnRate = Math.PI / 160;

        origObjImg = null;
        try {
            origObjImg =
              ImageIO.read(getClass().getResource("playership.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        diam = origObjImg.getWidth();
    }
}
