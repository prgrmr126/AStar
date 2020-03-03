import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pathfinder {

	private List<Node> openList = new ArrayList<Node>();
	private List<Node> closedList = new ArrayList<Node>();

	private List<Node> wallsList, foundList;

	private Node startNode, endNode, maxNode;

	private boolean found, dead;

	public Pathfinder(Node start, Node end, List<Node> walls, Node max) {

		startNode = start;
		endNode = end;
		wallsList = walls;

		openList = new ArrayList<Node>();
		closedList = new ArrayList<Node>();

		openList.add(startNode);

		maxNode = max;

		found = false;
		dead = false;

		startNode.calculateH(endNode);
		startNode.calculateF();
	}

	// Does one step of finding the path
	public void find() {

		// Gets node with the lowest f value

		int currentIndex = getLowestF();
		Node currentNode = openList.get(currentIndex);

		// Moves the node to the closed list

		openList.remove(currentIndex);
		closedList.add(currentNode);

		// If the node is the end node then the path has been found!

		if (currentNode.equals(endNode)) {
			buildPath(currentNode);
			return;
		}

		// Possible changes for all eight or four directions

		Node[] directions;

		if (Settings.diagonalAllowed == true) {
			int i = 0;
			directions = new Node[8];
			
			for (int a = -1; a <= 1; a++) {
				for (int b = -1; b <= 1; b++) {
					if (a == 0 && b == 0) continue;
					directions[i] = new Node(a, b);
					i++;
				}
			}
		}
		else {
			int i = 0;
			directions = new Node[4];
			
			for (int a = -1; a <= 1; a++) {
				for (int b = -1; b <= 1; b++) {
					if (Math.abs(a + b) != 1) continue;
					directions[i] = new Node(a, b);
					i++;
				}
			}
		}


		// Adds children to open list if they meet all requirements

		for (Node direction : directions) {
			Node child = new Node(currentNode, currentNode.getX() + direction.getX(), currentNode.getY() + direction.getY());

			if (Settings.diagonalAllowed == true) {
				int xor = direction.getX() ^ direction.getY();

				if (!(xor == direction.getX() || xor == direction.getY())) {
					boolean horPosFound = Pathfinder.nodeSearch(wallsList, new Node(currentNode.getX(), currentNode.getY() + direction.getY()));
					boolean verPosFound = Pathfinder.nodeSearch(wallsList, new Node(currentNode.getX() + direction.getX(), currentNode.getY()));

					// Cutting around walls and going through wall "gaps"
					if (horPosFound || verPosFound) {
						continue;
					}
				}
			}

			// Out of bounds
			if ((child.getX() > maxNode.getX()) || (child.getX() < 0) || (child.getY() > maxNode.getY()) || (child.getY() < 0)) continue;
			// Is a wall
			else if (Pathfinder.nodeSearch(wallsList, child)) continue;
			// Is in closed list already
			else if (Pathfinder.nodeSearch(closedList, child)) continue;

			// Node calculations
			child.calculateG(currentNode);
			child.calculateH(endNode);
			child.calculateF();

			// If there no identical node with a lower G cost, add it!
			if (!identicalLowerG(child)) openList.add(child);
		}

		// No open nodes mean no possible paths :(
		if (openList.size() <= 0) {
			dead = true;
			found = true;
		}
	}

	// Creates path back to source
	public void buildPath(Node finalNode) {
		List<Node> path = new ArrayList<Node>();
		Node current = finalNode;

		while (current != null) {
			path.add(new Node(current.getX(), current.getY()));
			current = current.getParent();
		}

		Collections.reverse(path);
		found = true;
		foundList = path;
	}

	// Returns the index of the the node with the lowest F cost in the open list
	public int getLowestF() {
		double lowestF = Integer.MAX_VALUE;
		int currentIndex = 0;
		for (int i = 0; i < openList.size(); i++) {
			Node thisNode = openList.get(i);
			if (thisNode.getF() < lowestF) {
				lowestF = thisNode.getF();
				currentIndex = i;
			}
		}

		return(currentIndex);
	}

	// Returns if there is a identical in the open list with a lower G cost
	public boolean identicalLowerG(Node child) {
		boolean breakNow = false;
		int temp = -1;

		for (int i = 0; i < openList.size(); i++) {
			Node openNode = openList.get(i);
			if (child.equals(openNode)) {

				if ((openNode.getG() <= child.getG())) return(true);
				else if ((openNode.getG() > child.getG())) {
					temp = i;
					breakNow = true;
				}
			}

			if (breakNow) break;
		}

		if (temp != -1) openList.remove(temp);

		return(false);
	}

	public List<Node> getOpenList() {
		return(openList);
	}

	public List<Node> getClosedList() {
		return(closedList);
	}

	public List<Node> getPath() {
		return(foundList);
	}

	public boolean isFound() {
		return(found);
	}

	public boolean isDead() {
		return(dead);
	}

	public static boolean nodeSearch(List<Node> arr, Node search) {
		for (Node item : arr) {
			if (item.equals(search)) return(true);
		}
		return(false);
	}
}
