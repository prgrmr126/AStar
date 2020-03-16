import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import java.util.ArrayList;
import java.util.List;

public class Visual extends JPanel implements KeyListener, MouseListener, MouseMotionListener, ActionListener {
	private static final long serialVersionUID = 1L;

	private final int nodeSize = Settings.nodeSize;

	private boolean finding;
	private char currentKey;

	private JFrame window;

	private Pathfinder finder;

	private Node startNode, endNode;

	private List<Node> wallsList, openedList, closedList, foundList;

	private Timer timer;

	public Visual() {

		// Pathfinder

		finding = false;

		openedList = new ArrayList<Node>();
		closedList = new ArrayList<Node>();

		foundList = new ArrayList<Node>();
		wallsList = new ArrayList<Node>();

		// Set up

		window = new JFrame();
		window.setContentPane(this);
		window.setTitle("A* Pathfinding Visualization");
		window.getContentPane().setPreferredSize(new Dimension(Settings.initialHeight, Settings.initialWidth));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);

		setSize(Settings.initialHeight, Settings.initialWidth);
		setLayout(null);
		setVisible(true);

		// Listeners

		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);

		window.addKeyListener(this);
		window.addMouseListener(this);

		timer = new Timer(Settings.timerDelay, this);
	}

	// Run immediately causes an immediate finding of the path without showing steps
	public void runImmediately() {

		System.out.println("STATUS: FINDING");

		finding = true;

		if ((startNode != null) && (endNode != null)) {

			Node max;
			int maxWidth;
			int maxHeight;

			maxWidth = getWidth() % Settings.nodeSize == 0 ? (getWidth() - nodeSize) / nodeSize
					: ((getWidth() - nodeSize) / nodeSize) + 1;

			maxHeight = getHeight() % Settings.nodeSize == 0 ? (getHeight() - nodeSize) / nodeSize
					: ((getHeight() - nodeSize) / nodeSize) + 1;

			max = new Node(maxWidth, maxHeight);

			finder = new Pathfinder(startNode, endNode, wallsList, max);
		}
		else {
			return;
		}

		while (!finder.isFound() && !finder.isDead() && finding) {
			finder.find();
		}

		openedList = finder.getOpenList();
		closedList = finder.getClosedList();
		complete();
	}

	// Run will initialize finder and start swing timer.
	public void run() {

		System.out.println("STATUS: FINDING");

		finding = true;

		Node max;
		int maxWidth;
		int maxHeight;

		maxWidth = getWidth() % Settings.nodeSize == 0 ? (getWidth() - nodeSize) / nodeSize
				: ((getWidth() - nodeSize) / nodeSize) + 1;

		maxHeight = getHeight() % Settings.nodeSize == 0 ? (getHeight() - nodeSize) / nodeSize
				: ((getHeight() - nodeSize) / nodeSize) + 1;

		max = new Node(maxWidth, maxHeight);

		finder = new Pathfinder(startNode, endNode, wallsList, max);

		timer.start();
	}

	// Action performed triggered every time the swing timer is set off and
	// Pathfinder's find method is called
	@Override
	public void actionPerformed(ActionEvent e) {
		if (!finder.isFound() && !finder.isDead() && finding) {
			finder.find();

			openedList = finder.getOpenList();
			closedList = finder.getClosedList();

			repaint();

			timer.restart();
		}
		else if (finding == true) {
			complete();
		}
	}

	// Once complete stops finding and prints status
	public void complete() {
		if (finder.isDead()) {
			System.out.println("STATUS: DEAD");
		}
		else {
			System.out.println("STATUS: FOUND");
			foundList = finder.getPath();
		}

		finding = false;

		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Paints grid
		g.setColor(Color.LIGHT_GRAY);

		for (int x = 0; x < getHeight(); x += nodeSize) {
			for (int y = 0; y < getWidth(); y += nodeSize) {
				g.drawRect(y, x, nodeSize, nodeSize);
			}
		}

		// Paints all nodes in open list as orange
		g.setColor(Color.ORANGE);
		for (Node open : openedList) {
			g.fillRect(open.getX() * nodeSize + 1, open.getY() * nodeSize + 1, nodeSize - 1, nodeSize - 1);
		}

		// Paints all nodes in closed list as red
		g.setColor(Color.RED);
		for (Node closed : closedList) {
			g.fillRect(closed.getX() * nodeSize + 1, closed.getY() * nodeSize + 1, nodeSize - 1, nodeSize - 1);
		}

		// Paints the found path in the found list as cyan ONCE FOUND
		g.setColor(Color.CYAN);
		if (foundList != null) {
			for (Node pos : foundList) {
				g.fillRect(pos.getX() * nodeSize + 1, pos.getY() * nodeSize + 1, nodeSize - 1, nodeSize - 1);
			}
		}

		// Paints the walls Nodes from the walls list as black
		if (wallsList != null) {
			g.setColor(Color.BLACK);
			for (Node wall : wallsList) {
				g.fillRect(wall.getX() * nodeSize + 1, wall.getY() * nodeSize + 1, nodeSize - 1, nodeSize - 1);
			}
		}

		// Paints the start Node as green
		if (startNode != null) {
			g.setColor(Color.GREEN);
			g.fillRect(startNode.getX() * nodeSize + 1, startNode.getY() * nodeSize + 1, nodeSize - 1, nodeSize - 1);

		}

		// Paints the end Node as blue
		if (endNode != null) {
			g.setColor(Color.BLUE);
			g.fillRect(endNode.getX() * nodeSize + 1, endNode.getY() * nodeSize + 1, nodeSize - 1, nodeSize - 1);

		}
	}

	// Handles all incoming mouse movements
	public void eventHandler(MouseEvent e) {
		if (finding) {
			return;
		}

		Node itemPos = new Node(e.getX() / nodeSize, e.getY() / nodeSize); // Maps pixel value to actual value

		// If user is trying to place a Node of some kind
		if (SwingUtilities.isLeftMouseButton(e)) {

			for (int i = 0; i < wallsList.size(); i++) {
				if (wallsList.get(i).equals(itemPos)) {
					return;
				}
			}

			if (itemPos.equals(startNode) || itemPos.equals(endNode)) {
				return;
			}

			// add start
			if (currentKey == 's') {
				startNode = itemPos;
				repaint();
			}
			// add end
			else if (currentKey == 'e') {
				endNode = itemPos;
				repaint();
			}
			// add wall
			else {
				wallsList.add(itemPos);
				repaint();
			}
		}
		// If user is trying to remove a Node of some kind
		else if (SwingUtilities.isRightMouseButton(e)) {

			if (itemPos.equals(startNode)) {
				startNode = null;
				repaint();
			}

			else if (itemPos.equals(endNode)) {
				endNode = null;
				repaint();
			}

			else {
				int removeIndex = -1;
				for (int i = 0; i < wallsList.size(); i++) {
					if (wallsList.get(i).equals(itemPos)) {
						removeIndex = i;
					}
				}
				if (removeIndex != -1) {
					wallsList.remove(removeIndex);
					repaint();
				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		eventHandler(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		eventHandler(e);
	}

	@Override
	// Changes the paint type once a key bind is pressed
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		currentKey = key;

		// Space triggers the path finding
		if (currentKey == ' ') {
			if (startNode == null || endNode == null) {
				System.out.println("STATUS: START AND END NODE REQUIRED TO RUN");
				return;
			}
			if (Settings.immediateRun) {
				runImmediately();
			}
			else {
				run();
			}
		}
		// Force quits program
		else if (currentKey == 'q') {
			System.exit(0);
		}
		// Clears the screen of all Nodes, nodes, etc.
		else if (currentKey == 'c') {
			if (finding)
				finding = false;

			if (!(openedList == null))
				openedList.clear();
			if (!(closedList == null))
				closedList.clear();

			if (!(wallsList == null))
				wallsList.clear();
			if (!(foundList == null))
				foundList.clear();

			startNode = null;
			endNode = null;

			System.out.println("STATUS: CLEARING");

			repaint();
		}
		// Resets the screen by clearing open, closed, found, keeps walls and start and
		// end Nodes.
		else if (currentKey == 'r') {
			if (finding)
				finding = false;

			if (!(openedList == null))
				openedList.clear();
			if (!(closedList == null))
				closedList.clear();
			if (!(foundList == null))
				foundList.clear();

			System.out.println("STATUS: RESET");

			repaint();
		}
	}

	@Override
	// Sets the paint type to walls after releasing key
	public void keyReleased(KeyEvent e) {
		currentKey = (char) 0;
	}

	// Required implement methods
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	public static void main(String[] args) {
		new Visual();
	}
}
