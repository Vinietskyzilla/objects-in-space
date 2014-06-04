import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
public class Fighter extends SpaceObj {
	protected Fighter() {}
	public Fighter(int X, int Y, int sID) {
		// pass maxVelocity to SpaceObj
		super(sID / 3);
		
		name = "Fighter";
		shipID = sID;
		
		weapons.add(Weapon.LB);
		structInteg = 50;
		
		angle = Math.random()*2*Math.PI;
		x = X;// - .5*origObjImg.getWidth();
		y = Y;// - .5*origObjImg.getHeight();
		dx = 0;
		dy = 0;	// y increases UPWARD
		accelRate = .06;
		turnRate = Math.random()*Math.PI / (1200) + Math.PI / (1200);
		isAccel = 1;
		if(Math.random() > .5)
			turningRight = true;
		else
			turningLeft = true;
		firing = 1;
		
		origObjImg = null;
		try {
			//origObjImg = ImageIO.read(new File("fighter.png"));
			//origObjImg = new BufferedImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/fighter.png")));
			origObjImg = ImageIO.read(getClass().getResource("fighter.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		diam = origObjImg.getWidth();
	}
}