package ffpf.git;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Pathfinder {
	public static float CELL_SIZE = 12f;
	// we traverse the scene for objects and adjust 
	public static int[][] envCostGrid;						
	public static int	NUM_X_NODES,
						NUM_Y_NODES,
						NODE_SIZE;
	
	private int[][] localCostGrid = new int[NUM_X_NODES][NUM_Y_NODES];
	
	int goalX, goalY, currentX, currentY;
	
	public Pathfinder(int currentX, int currentY, int goalX, int goalY) {
		this.goalX = goalX/NODE_SIZE;
		this.goalY = goalY/NODE_SIZE;
		this.currentX = currentX/NODE_SIZE;
		this.currentY = currentY/NODE_SIZE;
		System.out.println(this.goalX + ", " + this.goalY);
		generateLocalizedCostGrid();
	}
	
	/***
	 * We can limit our search area by just using the unit circle
	 * or some trig functions.
	 * 
	 * -1,-1	0,-1	1,-1
	 * -1,0		0,0		1,0
	 * -1,1		0,1		1,1
	 * 
	 */
	
	// generate cost grid 
	private void generateLocalizedCostGrid() {
		for(int i = 0; i < NUM_X_NODES; i++) {
			for(int j = 0; j < NUM_Y_NODES; j++) {
				localCostGrid[i][j] = envCostGrid[i][j] + Math.abs(i-goalX) + Math.abs(j-goalY);
//				System.out.print(localCostGrid[i][j] + "\t\t");
			}
//			System.out.println();
		}
		localCostGrid[goalX][goalY] = 0;
//		System.out.println(goalX + ", " + goalY);
	}
	
	public int[] moveAlongPath() {
		int[] newLocation = findLeastNeighbor();
		currentX = newLocation[0];
		currentY = newLocation[1];
		newLocation[0] *= NODE_SIZE;
		newLocation[1] *= NODE_SIZE;
		return newLocation;
	}
	
	private int[] findLeastNeighbor() {
		int[] leastNeighbor = new int[2];
		int leastNeighborValue = Integer.MAX_VALUE;
//		System.out.println("currentx,y: " + currentX + ","+currentY + ","+ localCostGrid[currentX][currentY]) ;
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				int tmp = -1;
				if(currentX+i < localCostGrid.length && currentY+j < localCostGrid[0].length
						&& currentX+i > 0 && currentY+j > 0 &&
						i+j != 0) {
					tmp = localCostGrid[currentX+i][currentY+j];
					if(tmp <= leastNeighborValue) {
						leastNeighborValue = tmp;
						leastNeighbor = new int[]{currentX+i, currentY+j};
					}
				}
//				System.out.print((currentX + i) + "," + (currentY + j) + "," + tmp + " ");
			}
//			System.out.println();
		}
		localCostGrid[leastNeighbor[0]][leastNeighbor[1]] += 100;
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
