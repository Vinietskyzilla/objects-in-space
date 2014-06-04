import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
public class PlayerShip extends SpaceObj {
	private PlayerShip() {}
	public PlayerShip(int X, int Y) {
		origObjImg = null;
		try {
			//origObjImg = ImageIO.read(new File("playership.png"));
			//origObjImg = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/playership.png"));
			
			origObjImg = ImageIO.read(getClass().getResource("playership.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		angle = .5*Math.PI;
		name = "PlayerShip";
		//gamePos = initGamePos;
		x = X;// - .5*origObjImg.getWidth();
		y = Y;// - .5*origObjImg.getHeight();
		dx = 0;
		dy = 0;	// y increases UPWARD
		maxVelocity = 20; // IMPORTANT! make sure this has the correct value before the first call to actionPanel.play().
		accelRate = 1;
		turnRate = Math.PI / 50;
	}
	public void fire() {}
}
