import java.io.*;
import javax.imageio.ImageIO;
public class Fighter extends SpaceObj {
	private Fighter() {}
	public Fighter(int X, int Y) {
		origObjImg = null;
		try {
			origObjImg = ImageIO.read(new File("fighter.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		angle = Math.random()*2*Math.PI;
		name = "Fighter";
		//gamePos = initGamePos;
		x = X;// - .5*origObjImg.getWidth();
		y = Y;// - .5*origObjImg.getHeight();
		dx = 5;
		dy = 1;	// y increases UPWARD
		maxVelocity = 150;
		accelRate = 6;
	}
}