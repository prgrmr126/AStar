
public class Point {
	private int x;
	private int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public void setPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Point point) {
		return ((this.x == point.getX()) && (this.y == point.getY()));
	}
	
	public Point diff(Point point) {
		return (new Point(this.x - point.getX(), this.y - point.getY()));
	}
}
