public class Position {
	public double x;
	public double y;
	public Position() {}
	public Position(double arg1, double arg2) {
		x = arg1;
		y = arg2;
	}
	public PositionInt getPositionInt() {
		return new PositionInt((int) x, (int) y);
	}
}