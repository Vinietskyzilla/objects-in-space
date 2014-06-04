import java.io.*;
import javax.imageio.ImageIO;
//import java.awt.Toolkit;
import java.awt.image.BufferedImage;
public class PlayerShip extends SpaceObj {
	int structIntegInit;
	protected PlayerShip() {}
	public PlayerShip(int X, int Y, int sID) {
		// pass maxVelocity to SpaceObj
		super(5); // IMPORTANT! make sure this has the correct value before the first call to actionPanel.play().
		
		name = "PlayerShip";
		shipID = sID;
		
		weapons.add(Weapon.LB);
		structIntegInit = 150;
		structInteg = structIntegInit;
		
		angle = .5*Math.PI;
		x = X;// - .5*origObjImg.getWidth();
		y = Y;// - .5*origObjImg.getHeight();
		dx = 0;
		dy = 0;	// y increases UPWARD
		accelRate = .2;
		turnRate = Math.PI / 160;
		
		origObjImg = null;
		try {
			//origObjImg = ImageIO.read(new File("playership.png"));
			//origObjImg = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/playership.png"));
			
			origObjImg = ImageIO.read(getClass().getResource("playership.png"));//"playership.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		diam = origObjImg.getWidth();
	}
	public void fire() {}
}
