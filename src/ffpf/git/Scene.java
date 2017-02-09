package ffpf.git;

import java.awt.Color;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.JPanel;

public class Scene extends JPanel implements KeyListener, MouseListener {	

	public static int WORLD_WIDTH			= 640,
						WORLD_HEIGHT		= 640,
						WINDOW_WIDTH		= 640,
						WINDOW_HEIGHT		= 640,
						
						OBSTACLE_COUNT		= 10,
						
						OBSTACLE_MIN_WIDTH	= 12,
						OBSTACLE_MAX_WIDTH	= 30,
						
						OBSTACLE_MIN_HEIGHT	= 12,
						OBSTACLE_MAX_HEIGHT	= 30,
						
						NODE_SIZE 			= 8;
	
	private int numXNodes = WORLD_WIDTH/NODE_SIZE,
			numYNodes = WORLD_HEIGHT/NODE_SIZE;
	
	List<Obstacle> obstacles = new ArrayList<Obstacle>();
//	List<Obstacle> guys = new ArrayList<Obstacle>();
	int lastId = 0;
	
	int[] guys;
	
	List<Pathfinder> pathfinders = new ArrayList<Pathfinder>();
	
	HashMap<Point, Integer> globalHashMap = new HashMap<Point, Integer>();
	HashSet<Integer> ghmPoints = new HashSet<Integer>();
	
	HashSet<Guy> theGuys = new HashSet<Guy>();
	
	Pathfinder currentPathfinder = null; 
	
	public class Guy {
		public Obstacle guy;
		public Pathfinder currentPath;
		public Guy(Obstacle guy, Pathfinder currentPath) {
			this.guy = guy;
			this.currentPath = currentPath;
		}
	}
	
	Random rng;
	
	int[][] envCostGrid = new int[numXNodes][numYNodes];
	
	int[][] changeArray = new int[numXNodes][numYNodes];

	private boolean drawPathfinderHeatmap = false;
	
	public Scene() {
		Pathfinder.NUM_X_NODES = numXNodes;
		Pathfinder.NUM_Y_NODES = numYNodes;
		// Frame & input setup
		addMouseListener(this);
		addKeyListener(this);
		setLayout(null);
		setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
		setBackground(Color.gray);
//		setResizable(false);
		setVisible(true);
		rng = new Random();
		generateRandomObstacles();
		generateEnvironmentCostGrid();
		Pathfinder.envCostGrid = envCostGrid.clone();
		Pathfinder.NODE_SIZE = NODE_SIZE;
		
		guys = new int[10];
		
		for(int i = 0; i < 10; i++) {
			Obstacle newGuy = new Obstacle(rng.nextInt(WORLD_WIDTH), rng.nextInt(WORLD_HEIGHT), 8, 8, lastId++, 50);
			newGuy.color = new Color(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255));
			guys[i] = newGuy.id; 
			theGuys.add(new Guy(newGuy, null));
			obstacles.add(newGuy);
		}
	}
	
	public Scene(int seed) {
		this();
		rng = new Random(seed);
		obstacles.clear();
		generateRandomObstacles();
	}
	
	private void generateRandomObstacles() {
		for(int i = 0; i < OBSTACLE_COUNT; i++) {
			int randW = rng.nextInt(OBSTACLE_MAX_WIDTH) + OBSTACLE_MIN_WIDTH;
			int randH = rng.nextInt(OBSTACLE_MAX_HEIGHT) + OBSTACLE_MIN_HEIGHT;
			int randX = rng.nextInt(WORLD_WIDTH-OBSTACLE_MAX_WIDTH) + OBSTACLE_MAX_WIDTH;
			int randY = rng.nextInt(WORLD_HEIGHT-OBSTACLE_MAX_HEIGHT) + OBSTACLE_MAX_HEIGHT;
			obstacles.add(new Obstacle(randX, randY, randW, randH, lastId++, 10000));
		}
	}
	
	private void generateEnvironmentCostGrid() {
		for(int i = 0; i < numXNodes; i++) {
			for(int j = 0; j < numYNodes; j++) {
				for(Obstacle o : obstacles){
					Rectangle rect1 = new Rectangle(i*NODE_SIZE, j*NODE_SIZE, NODE_SIZE, NODE_SIZE);
					Rectangle rect2 = new Rectangle(o.x, o.y, o.w, o.h);
					Rectangle intersection = rect1.intersection(rect2);
					if(!intersection.isEmpty()) {
						envCostGrid[i][j] = o.cost;
					}
				}
//				System.out.print(envCostGrid[i][j] + " ");
			}
//			System.out.println();
		}
//		System.out.println("# of nodes with cost = " + costNodes);
		
	}
	
	public void getGlobalHashMap() {
		for(Pathfinder p : pathfinders) {
			HashMap<Point, Integer> tmp = p.getChangeList();
			for(Entry<Point, Integer> entry : tmp.entrySet()) {
				Point k = entry.getKey();
				changeArray[k.x][k.y] += entry.getValue();
			}
		}
	}
	
	public void updateLocalHashMaps() {
		for(Pathfinder p : pathfinders) {
			p.loadChangeList(changeArray);
		}
	}
	
	public void update() {
		
	}
	
	public void paintComponent(Graphics g)
	{
		/*
		if(envCostGrid != null) {
			for(int i = 0; i < numXNodes; i++) {
				for(int j = 0; j < numYNodes; j++) {
					g.setColor(Color.black);
					g.drawRect(i*NODE_SIZE, j*NODE_SIZE, NODE_SIZE, NODE_SIZE);
					if(envCostGrid[i][j] > 0) {
//						System.out.println("high base cost found");
						g.setColor(Color.red);
						g.fillRect((i*NODE_SIZE)-1, (j*NODE_SIZE)+1, NODE_SIZE+2, NODE_SIZE-2);
					}
				}
			}
			painted = true;
		}
		*/
		if(drawPathfinderHeatmap ) {
			if(pathfinders.size() > 0) {
				Pathfinder currentPathFinder = pathfinders.get(pathfinders.size()-1);
				if(currentPathFinder != null)
					currentPathFinder.paint(g);
			}
		}
		
		for(Obstacle o:obstacles)
			o.paint(g);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
			System.exit(0);
		if(arg0.getKeyCode() == KeyEvent.VK_SPACE) {
			if(guys != null) {
				//System.out.println(guys.length);
				for(Guy g : theGuys) {
					if(g.currentPath != null) {
						g.currentPath.moveAlongPath(g.guy.id);
						Point pos = g.currentPath.getLocation(g.guy.id);
						//System.out.println(pos);
						if(pos != null) {
							g.guy.x = pos.x;
							g.guy.y = pos.y;
						}
					}
					getGlobalHashMap();
					updateLocalHashMaps();
				}
				repaint();
			}
		}
		if(arg0.getKeyCode() == KeyEvent.VK_G) {
			drawPathfinderHeatmap = !drawPathfinderHeatmap;
			repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		Pathfinder tmp = new Pathfinder(arg0.getX(), arg0.getY());
		for(Guy g : theGuys) {
			tmp.addUser(g.guy.x, g.guy.y, g.guy.id);
			g.currentPath = tmp;
		}
		pathfinders.add(tmp);
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
