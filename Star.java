import java.awt.Color;
public class Star extends SpaceObj {
	public int diam;
	public Color color;
	protected Star() {}
	public Star(int width, int height) {
		// maxVelocity == 0
		super(0);
		int signX = -1;
		if(Math.random() > .5)
			signX = 1;
		int signY = -1;
		if(Math.random() > .5)
			signY = 1;
		x = (Math.random() * (width / 2)) * signX;
		y = (Math.random() * (height / 2)) * signY;
		//System.out.println("int (" + x + ", " + y + ")");
		diam = (int) (Math.random() * 5);
		int temp = (int) (Math.random() * 0x00FFFFFF);
		//System.out.println(String.format("Random int %x", temp));
		color = new Color(temp | 0xFF000000);
		//System.out.println(String.format("My color is %x", color.getRGB()));
	}
}

// System.out.println(String.format("(%.1f, %.1f)", Math.random() * width, Math.random() * height));
// x = (int) (Math.random() * width);
// y = (int) (Math.random() * height);
// System.out.println("int (" + x + ", " + y + ")");