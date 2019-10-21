import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pathfinder {

	private List<Node> openList = new ArrayList<Node>();
	private List<Node> closedList = new ArrayList<Node>();

	private List<Node> wallsList;
	private List<Node> foundList;

	private Node startNode;
	private Node endNode;
	private Node maxNode;

	private boolean found;
	private boolean dead;

	public Pathfinder(Node start, Node end, List<Node> walls, Node max) {

		this.startNode = start;
		this.endNode = end;
		this.wallsList = walls;

		this.openList = new ArrayList<Node>();
		this.closedList = new ArrayList<Node>();

		this.openList.add(this.startNode);

		this.maxNode = max;

		this.found = false;
		this.dead = false;

		this.startNode.setH(this.startNode.getX() - this.endNode.getX(), this.startNode.getY() - this.endNode.getY());
		this.startNode.calculateF();
	}

	public void find() {

		// Gets node with the lowest f value

		Node currentNode = this.openList.get(0);
		int currentIndex = 0;

		for (int i = 0; i < this.openList.size(); i++) {
			Node thisNode = this.openList.get(i);
			if (thisNode.getF() < currentNode.getF()) {
				currentNode = thisNode;
				currentIndex = i;
			}
		}

		// Moves the node to the closed list

		this.openList.remove(currentIndex);
		this.closedList.add(currentNode);

		// If the node is the end node then the path has been found!

		if (currentNode.equals(this.endNode)) {
			List<Node> route = new ArrayList<Node>();
			Node current = currentNode;
			while (current != null) {
				route.add(current.getPos());
				current = current.getParent();
			}
			Collections.reverse(route);
			this.found = true;
			this.foundList = route;
		}

		// Possible changes for all eight directions

		Node[] positions = new Node[] {new Node(0, -1), new Node(0, 1), new Node(-1, 0), new Node(1, 0),
									   new Node(-1, -1), new Node(-1, 1), new Node(1, -1), new Node(1, 1)};


		// Adds children to open list if they meet all requirements

		for (Node changePos : positions) {

			Node child = new Node(currentNode, currentNode.getX() + changePos.getX(), currentNode.getY() + changePos.getY());

			int xor = changePos.getX() ^ changePos.getY();

			if (!(xor == changePos.getX() || xor == changePos.getY())) {
				Node horPos = new Node(currentNode.getX(), currentNode.getY() + changePos.getY());
				Node verPos = new Node(currentNode.getX() + changePos.getX(), currentNode.getY());

				// Wall "gaps"
				if (Pathfinder.nodeSearch(this.wallsList, horPos) && Pathfinder.nodeSearch(this.wallsList, verPos)) {
					continue;
				}
				// Cutting around walls
				else if (Pathfinder.nodeSearch(this.wallsList, horPos) || Pathfinder.nodeSearch(this.wallsList, verPos)) {
					continue;
				}
			}

			// Out of bounds, a wall, or in closed list
			if ((child.getX() > this.maxNode.getX()) || (child.getX() < 0) || (child.getY() > this.maxNode.getY()) || (child.getY() < 0)) continue;
			else if (Pathfinder.nodeSearch(this.wallsList, child)) continue;
			else if (Pathfinder.nodeSearch(this.closedList, child)) continue;

			// Node calculations
			child.setG(currentNode);
			child.setH(currentNode.getX() - endNode.getX(), currentNode.getY() - endNode.getY());
			child.calculateF();

			boolean lowerGFound = false;

			// Identical child with a lower g
			for (Node openNode : this.openList) {
				if (child.equals(openNode) && (child.getG() > openNode.getG())) {
					lowerGFound = true;
					break;
				}
			}

			if (!lowerGFound) this.openList.add(child);
		}

		// No open nodes mean not paths :(
		if (this.openList.size() <= 0) {
			this.dead = true;
			this.found = true;
		}
	}

	public List<Node> getOpenList() {
		return openList;
	}

	public List<Node> getClosedList() {
		return closedList;
	}

	public List<Node> getPath() {
		return foundList;
	}

	public boolean isFound() {
		return found;
	}

	public boolean isDead() {
		return dead;
	}

	public static boolean nodeSearch(List<Node> arr, Node search) {
		for (Node item : arr) {
			if (item.equals(search)) return(true);

		}
		return(false);
	}
}
