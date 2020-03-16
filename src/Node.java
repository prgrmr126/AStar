public class Node {

	private Node parent;

	private int x, y;

	private double g, h, f;

	public Node(Node parent, int x, int y) {
		this.parent = parent;

		this.x = x;
		this.y = y;

		g = 0;
		h = 0;
		f = 0;
	}

	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Node getParent() {
		return parent;
	}

	public double getG() {
		return g;
	}

	public double getH() {
		return h;
	}

	public double getF() {
		return f;
	}

	public void calculateG(Node parent) {

		if (Settings.diagonalAllowed) {
			int diffX = x - parent.getX();
			int diffY = y - parent.getY();

			int xor = diffX ^ diffY;

			if (xor == diffX || xor == diffY) {
				// Straight, left, right, backwards have a g cost of 1
				g = parent.getG() + 1;
			}
			else {
				// Diagonals have g cost of ≈ 1.4142 because of Pythagorean's theorem.
				// Initialized in Settings class to save time of calculation square root of 2
				g = parent.getG() + Settings.diagonalCost;
			}
		}
		else {
			g = parent.getG() + 1;
		}
	}

	public void calculateH(Node endNode) {

		int xDiff = Math.abs(x - endNode.getX());
		int yDiff = Math.abs(y - endNode.getY());

		if ((xDiff == 0) && (yDiff == 0)) {
			h = 0;
		}
		else if (xDiff == 0) {
			h = yDiff;
		}
		else if (yDiff == 0) {
			h = xDiff;
		}
		else if (Settings.diagonalAllowed == true) {
			// Diagonal heuristic
			h = (xDiff + yDiff) + (Settings.diagonalCost - 2) * Math.min(xDiff, yDiff);
		}
		else {
			// Manhattan heuristic
			h = xDiff + yDiff;
		}

		h *= Settings.heuristicWeighting;
	}

	public void calculateF() {
		f = h + g;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Node) {
			Node node = (Node) obj;
			return x == node.getX() && y == node.getY();
		}
		else {
			return false;
		}
	}
}
