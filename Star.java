import java.awt.Color;
/**

*/
public class Star extends SpaceObj {
  /**

  */
  public int diam;
  /**

  */
  public Color color;
  /**

  */
  protected Star() {}
  /**

  */
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
    diam = (int) (Math.random() * 5);
    int temp = (int) (Math.random() * 0x00FFFFFF);
    color = new Color(temp | 0xFF000000);
  }
}
