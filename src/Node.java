public class Node {

	private Node parent;
	
	private int x;
	private int y;
	
	private double g;
	private double h;
	private double f;
	
	public Node(Node parent, int x, int y) {
		this.parent = parent;
		
		this.x = x;
		this.y = y;
		
		this.g = 0;
		this.h = 0;
		this.f = 0;
	}
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return(this.x);
	}
	
	public int getY() {
		return(this.y);
	}
	
	public Node getParent() {
		return(this.parent);
	}
	
	public double getG() {
		return(this.g);
	}
	
	public double getH() {
		return(this.h);
	}
	
	public double getF() {
		return(this.f);
	}

	public void calculateG(Node parent) {
		
		if (Settings.diagonalAllowed) {
			int diffX = this.x - parent.getX();
			int diffY = this.y - parent.getY();
			
			int xor = diffX ^ diffY;
			
			if (xor == diffX || xor == diffY) {
				// Straight, left, right, backwards have a g cost of 1
				this.g = parent.getG() + 1;
			}
			else {
				// Diagonals have g cost of ≈ 1.4142 because of Pythagorean's theorem.
				// Initialized in Settings class to save time of calculation square root of 2
				this.g = parent.getG() + Settings.diagonalCost;
			}
		}
		else {
			this.g = parent.getG() + 1;
		}
	}
	
	public void calculateH(Node endNode) {
		
		int xDiff = Math.abs(this.x - endNode.getX());
		int yDiff = Math.abs(this.y - endNode.getY());
		
		if ((xDiff == 0) && (yDiff == 0)) {
			this.h = 0;
		}
		else if (xDiff == 0) {
			this.h = yDiff;
		}
		else if (yDiff == 0) {
			this.h = xDiff;
		}
		else if (Settings.diagonalAllowed == true){
			// Euclidean heuristic (pretty slow but accurate)
			//this.h = (double) (Math.sqrt((xDiff * xDiff) + (yDiff * yDiff)));
			
			// Diagonal heuristic
			//int dx = Math.abs(xDiff);
			//int dy = Math.abs(yDiff);
			this.h = (xDiff + yDiff) + (Settings.diagonalCost - 2 ) * Math.min(xDiff, yDiff);
		}
		else {
			// Manhattan heuristic
			this.h = xDiff + yDiff;
		}
		
		this.h *= Settings.heuristicWeighting;
	}

	public void calculateF() {
		this.f = this.h + this.g;
	}
	
	public boolean equals(Node node) {
		if (node == null) return false;
		return ((this.x == node.getX()) && (this.y == node.getY()));
	}
	
}