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
	public boolean keepGoing;
	private char currentKey;
	
	private JFrame window;
	
	private Pathfinder finder;
	
	private Node startNode;
	private Node endNode;
	
	private List<Node> wallsList;
	private List<Node> openedList;
	private List<Node> closedList;
	private List<Node> foundList;
	
	private Timer timer;
	
	public Visual(){
		
		// Pathfinder
		
		this.finding = false;
		
		this.openedList = new ArrayList<Node>();
		this.closedList = new ArrayList<Node>();
		
		this.foundList = new ArrayList<Node>();
		this.wallsList =  new ArrayList<Node>();
		
		// Set up
		
		window = new JFrame();
		window.setTitle("A* Visualizer");
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
		
		this.timer = new Timer(Settings.timerDelay, this);
	}
	
	
	
	//This run method causes an immediate finding of the path without showing steps
	
	
	public void runImmediately() {
		
		timer.setInitialDelay(1000);
		timer.start();
		
		
		System.out.println("STATUS: FINDING");
		
		this.finding = true;
		this.keepGoing = true;
		
		if ((this.startNode != null) && (this.endNode != null)) {
			
			Node max = new Node((this.getHeight() - this.nodeSize) / this.nodeSize, (this.getWidth() - this.nodeSize) / this.nodeSize);
						
			this.finder = new Pathfinder(this.startNode, this.endNode, this.wallsList, max);
			
			while ((!finder.isFound()) && (!finder.isDead()) && (this.keepGoing)) {
				finder.find();
								
				this.openedList = finder.getOpenList();
				this.closedList = finder.getClosedList();
				
				//if (this.getGraphics() != null) this.paintComponent(this.getGraphics());
				//else repaint();
				
				repaint();
			}
			
			this.complete();
		}
	}
	
	
	// This run method initializes all relevant variables are begins swing timer
	public void run() {
				
		System.out.println("STATUS: FINDING");
		
		this.finding = true;
		
		if ((this.startNode != null) && (this.endNode != null)) {
			
			Node max = new Node((this.getWidth() - this.nodeSize) / this.nodeSize, (this.getHeight() - this.nodeSize) / this.nodeSize);
						
			this.finder = new Pathfinder(this.startNode, this.endNode, this.wallsList, max);
			
			timer.start();
		}
	}
	
	// Action performed triggered every time timer is set off and Pathfinder's find method is called
	@Override
	public void actionPerformed(ActionEvent e) {
		if (!finder.isFound() && !finder.isDead() && this.finding) {
			finder.find();
			
			this.openedList = finder.getOpenList();
			this.closedList = finder.getClosedList();
			
			repaint();
			
			timer.restart();
		}
		else if (this.finding == true) {
			this.complete();
		}
	}
	
	// Once complete stops finding and prints status
	public void complete() {
		if (finder.isDead()) {
			System.out.println("STATUS: DEAD");
		}
		else {
			System.out.println("STATUS: FOUND");
		}
		
		this.foundList = finder.getPath();
		
		this.finding = false;
		
		repaint();
	}
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Paints grid
		g.setColor(Color.LIGHT_GRAY);
		
		for ( int x = 0; x < this.getHeight(); x += this.nodeSize ) {
			for ( int y = 0; y < this.getWidth(); y += this.nodeSize ) {				
				g.drawRect( y, x, this.nodeSize, this.nodeSize );
			}
		}
		
		// Paints all nodes in open list as orange
		g.setColor(Color.ORANGE);
		for (Node open : this.openedList) {
			g.fillRect( open.getX() * this.nodeSize + 1, open.getY() * this.nodeSize + 1, this.nodeSize - 1, this.nodeSize - 1);
		}
		
		// Paints all nodes in closed list as red
		g.setColor(Color.RED);
		for (Node closed : this.closedList) {
			g.fillRect( closed.getX() * this.nodeSize + 1, closed.getY() * this.nodeSize + 1, this.nodeSize - 1, this.nodeSize - 1);
		}
		
		// Paints the found path in the found list as cyan ONCE FOUND
		g.setColor(Color.CYAN);
		if (this.foundList != null) {
			for (Node pos : this.foundList) {
				g.fillRect( pos.getX() * this.nodeSize + 1, pos.getY() * this.nodeSize + 1, this.nodeSize - 1, this.nodeSize - 1);
			}
		}
		
		// Paints the walls Nodes from the walls list as black
		if (this.wallsList != null) {
			g.setColor(Color.BLACK);
			for (Node wall : this.wallsList) {
				g.fillRect( wall.getX() * this.nodeSize + 1, wall.getY() * this.nodeSize + 1, this.nodeSize - 1, this.nodeSize - 1);
			}
		}
		
		// Paints the start Node as green
		if (this.startNode != null) {
			g.setColor(Color.GREEN);
			g.fillRect( this.startNode.getX() * this.nodeSize + 1, this.startNode.getY() * this.nodeSize + 1, this.nodeSize - 1, this.nodeSize - 1);
			
		}
		
		// Paints the end Node as blue
		if (this.endNode != null) {
			g.setColor(Color.BLUE);
			g.fillRect( this.endNode.getX() * this.nodeSize + 1, this.endNode.getY() * this.nodeSize + 1, this.nodeSize - 1, this.nodeSize - 1);
			
		}
	}
	
	
	public void eventHandler(MouseEvent e) {
		if (this.finding) return;
				
		Node itemPos = new Node(e.getX() / this.nodeSize, e.getY() / this.nodeSize); // Maps pixel value to actual value
		
		// If user is trying to place a Node of some kind
		if (SwingUtilities.isLeftMouseButton(e)) {
			
			// add start
			if (currentKey == 's') {
				this.startNode = itemPos;
				repaint();
			}
			// add end
			else if (currentKey == 'e') {
				this.endNode = itemPos;
				repaint();
			}
			// add wall
			else {
				boolean alreadyExists = false;
				for (int i = 0; i < this.wallsList.size(); i++) {
					if (this.wallsList.get(i).equals(itemPos)) {
						alreadyExists = true;
					}
				}
				if (!alreadyExists) this.wallsList.add(itemPos);
				
				repaint();
			}
		}
		// If user is trying to remove a Node of some kind
		else if (SwingUtilities.isRightMouseButton(e)) {

			if (itemPos.equals(this.startNode)) {
				this.startNode = null;
				repaint();
			}

			else if (itemPos.equals(this.endNode)) {
				this.endNode = null;
				repaint();
			}

			else {
				int removeIndex = -1;
				for (int i = 0; i < this.wallsList.size(); i++) {
					if (this.wallsList.get(i).equals(itemPos)) {
						removeIndex = i;
					}
				}
				if (removeIndex != -1) {
					this.wallsList.remove(removeIndex);
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
			if (Settings.immediateRun) this.runImmediately();
			else this.run();
		}
		// Force quits program
		else if (currentKey == 'q') {
			System.exit(0);
		}
		// Clears the screen of all Nodes, nodes, etc.
		else if (currentKey == 'c') {
			if (this.finding) this.finding = false;
			
			if (!(this.openedList == null)) this.openedList.clear();
			if (!(this.closedList == null))this.closedList.clear();
			
			if (!(this.wallsList == null)) this.wallsList.clear();
			if (!(this.foundList == null)) this.foundList.clear();

			this.startNode = null;
			this.endNode = null;
			
			System.out.println("STATUS: CLEARING");
			
			repaint();
		}
		// Resets the screen by clearing open, closed, found, keeps walls and start and end Nodes.
		else if (currentKey == 'r') {
			if (this.finding) this.finding = false;
			
			if (!(this.openedList == null)) this.openedList.clear();
			if (!(this.closedList == null)) this.closedList.clear();
			
			if (!(this.foundList == null)) this.foundList.clear();
			
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
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mouseMoved(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	
	public static void main(String[] args) 
	{
		@SuppressWarnings("unused")
		Visual x = new Visual();
		System.out.println("STATUS: RUNNING");
		
	}
}
