package ffpf.git;

import java.awt.Color;
import java.awt.Graphics;

public class Obstacle extends ObstacleLocationData {
	Color color;
	public Obstacle(int x, int y, int w, int h, int cost) {
		super(x,y,w,h,cost);
		color = new Color(cost%255);
	}
	
	public void setColor(Color c) {
		color = c;
	}
	public void paint(Graphics g) {
		g.setColor(color);
		g.fillRect(x, y, w, h);
	}
}
