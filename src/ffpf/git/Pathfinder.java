package ffpf.git;

import java.util.ArrayList;

public class Pathfinder {
	public static float CELL_SIZE = 12f;
	// we traverse the scene for objects and adjust 
	private int[][] costGrid;
	
	// angle between start and end points
	private float interpathAngle = 0.0f;
	
	// if the angle between the start and goal leads to an increasing cost path, then we can narrow our search
	private boolean ipAngleWrong = false;
	
	public Pathfinder(float start, float goal) {
		
	}
	
	// includes a list of obstacles and their width and height
	public Pathfinder(float start, float goal, ArrayList<ObstacleLocationData> obstacles) {
		
	}
	
	// generate cost grid 
	private void generateCostGrid() {
		
	}
	
	private void findStartInGrid() {
		
	}
}
