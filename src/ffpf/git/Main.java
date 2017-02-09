package ffpf.git;

import javax.swing.JFrame;

public class Main extends JFrame {
	private static Scene scene;
	public static void main(String[] args) {
		add(scene);
		scene = new Scene();
		scene.update();
	}
}
