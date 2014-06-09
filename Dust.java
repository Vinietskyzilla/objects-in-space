import java.awt.Color;
public class Dust extends Star {
    protected Dust() {}
    public Dust(int width, int height) {
        super(width, height);
        diam = 3;
        color = new Color(0xFF808080);
    }
}
