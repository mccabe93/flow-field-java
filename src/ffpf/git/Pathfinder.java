package ffpf.git;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Pathfinder {
	public static float CELL_SIZE = 12f;
	// we traverse the scene for objects and adjust 
	public static int[][] envCostGrid;						
	public static int	NUM_X_NODES,
						NUM_Y_NODES,
						NODE_SIZE,
						GUY_COST = 50;
	
	private int userCount = 0;
	
	private int[][] localCostGrid = new int[NUM_X_NODES][NUM_Y_NODES];
	private int[][] baseLocalCostGrid = new int[NUM_X_NODES][NUM_Y_NODES];
	HashMap<Point, Integer> changeList = new HashMap<Point, Integer>();
	
	class User {
		HashSet<Integer> visited = new HashSet<Integer>();
		public int currentX, currentY, lastX, lastY;
		public User(int currentX, int currentY) {
			this.currentX = currentX/NODE_SIZE;
			this.currentY = currentY/NODE_SIZE;
			this.lastX = currentX/NODE_SIZE;
			this.lastY = currentY/NODE_SIZE;
		}
	}
	
	private HashMap<Integer, User> users = new HashMap<Integer, User>();
	
	int goalX, goalY;
	
	public Pathfinder(int goalX, int goalY) {
		this.goalX = goalX/NODE_SIZE;
		this.goalY = goalY/NODE_SIZE;
		generateLocalizedCostGrid();
	}
	
	public void loadChangeList(int[][] changeArray) {
		//for(Point p : list.keySet()) {
		//	localCostGrid[p.x][p.y] += list.get(p);
		//}
		for(int i = 0; i < changeArray.length; i++) {
			for(int j = 0; j < changeArray[0].length; j++) {
				localCostGrid[i][j] = baseLocalCostGrid[i][j] + changeArray[i][j];
			//	System.out.print(changeArray[i][j] + " ");
			}
//			System.out.println();
		}
	}
	
	public HashMap<Point, Integer> getChangeList() {
		HashMap<Point, Integer> oldChangeList = new HashMap<Point, Integer>(changeList);
		oldChangeList.putAll(changeList);
		changeList.clear();
		return oldChangeList;
	}
	
	// generate cost grid 
	private void generateLocalizedCostGrid() {
		for(int i = 0; i < NUM_X_NODES; i++) {
			for(int j = 0; j < NUM_Y_NODES; j++) {
				localCostGrid[i][j] = envCostGrid[i][j] + Math.abs(i-goalX) + Math.abs(j-goalY);
				baseLocalCostGrid[i][j] = localCostGrid[i][j];
//				System.out.print(localCostGrid[i][j] + "\t\t");
			}
//			System.out.println();
		}
		localCostGrid[goalX][goalY] = 0;
//		System.out.println(goalX + ", " + goalY);
	}
	
	public void moveAlongPath(int id) {
		User u = users.get(id);
		if(u == null)
			return;
		Point newLocation = findLeastNeighbor(u);
		
		u.lastX = u.currentX;
		u.lastY = u.currentY;
		
		u.currentX = newLocation.x;
		u.currentY = newLocation.y;
		
		u.visited.add(newLocation.getHash());
		
		int distX = Math.abs(u.currentX - goalX);	
		int distY = Math.abs(u.currentY - goalY);
		
		if(distX < 3 && distY < 3) {
			users.remove(id);
			userCount--;
		}
		else {
			changeList.put(new Point(u.lastX, u.lastY), -GUY_COST);
			changeList.put(new Point(u.currentX, u.currentY), GUY_COST);
		}
	}
	
	public void moveAllAlongPath() {
		for(Integer id : users.keySet()) {
			moveAlongPath(id);
		}
	}
	
	public void addUser(int x, int y, int id) {
		User user = new User(x, y);
		users.put(id, user);
		userCount++;
	}
	
	public int getUserCount() {
		return userCount;
	}
	
	public Point getLocation(int id) {
		User u = users.get(Integer.valueOf(id));
		if(u == null)
			return null;
		return new Point(u.currentX * NODE_SIZE, u.currentY * NODE_SIZE);
	}
	
	private Point findLeastNeighbor(User user) {
		Point leastNeighbor = new Point(user.currentX, user.currentY);
		
		int currentX = user.currentX,
				currentY = user.currentY;
		
		int leastNeighborValue = Integer.MAX_VALUE;
//		System.out.println("currentx,y: " + currentX + ","+currentY + ","+ localCostGrid[currentX][currentY]) ;
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				int tmp = -1;
				Point dest = new Point(currentX+i, currentY+j);
				boolean hasVisitied = user.visited.contains(dest.getHash());
				if(dest.x < localCostGrid.length && dest.y < localCostGrid[0].length
						&& dest.x > 0 && dest.y > 0 && 
						!hasVisitied &&
						i+j != 0) {
					tmp = localCostGrid[dest.x][dest.y];
					if(tmp <= leastNeighborValue) {
						leastNeighborValue = tmp;
						leastNeighbor = new Point(dest.x, dest.y);
					}
				}
//				System.out.print((currentX + i) + "," + (currentY + j) + "," + tmp + " ");
			}
//			System.out.println();
		}
		return leastNeighbor;
	}
	
	public void paint(Graphics g) {
		if(envCostGrid != null) {
			for(int i = 0; i < NUM_X_NODES; i++) {
				for(int j = 0; j < NUM_Y_NODES; j++) {
					g.setColor(Color.black);
					g.drawRect(i*NODE_SIZE, j*NODE_SIZE, NODE_SIZE, NODE_SIZE);
					g.setColor(new Color(localCostGrid[i][j]%255));
					g.fillRect((i*NODE_SIZE), (j*NODE_SIZE), NODE_SIZE, NODE_SIZE);
				}
			}
		}
	}
}
