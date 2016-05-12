package FastGame;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import Controllers.PiersController;
import Controllers.RandomController;
import runner.viewer.WASDController;

/**
 * Created by pwillic on 25/06/2015.
 */
public class FastGameMain {

	public static void main(String[] args) throws InterruptedException {

		JFrame frame = new JFrame("Game");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		WASDController wasd = new WASDController();
		// ArrowController arrows = new ArrowController();

		// Controller c1 = new GAController(true);

		// Controller c1 = new MCTS(true, 1000, 10, 45);
		// Controller c1 = new VariGA(true, 2000);
		PiersController c1 = new RandomController();

		// Controller c1 = new VariGA(true, 500);
		// Controller c1 = new RandomController();
		// Controller c1 = new NoOp();

		// Controller c1 = wasd;
		// Controller c2 = new GAController(false);
		// Controller c2 = new MCTS(false, 1000, 10, 45);
		// Controller c2 = new VariGA(false, 2000);
		PiersController c2 = new RandomController();
		// Controller c2 = arrows;
		// Controller c2 = new NoOp();

		CoopGame game = new CoopGame("data/maps/level1.txt");

		Viewer viewer = new Viewer(game);
		viewer.addKeyListener(wasd);
		// viewer.addKeyListener(arrows);
		viewer.setFocusable(true);
		viewer.requestFocus();

		frame.add(viewer);
		frame.pack();
		frame.setVisible(true);

		int ticks = 0;
		while (!game.hasWon()) {
			game.update(c1.get(game.getClone()), c2.get(game.getClone()));
			ticks++;
			// Thread.sleep(40);
			viewer.repaint();
		}
		System.out.println(ticks);
	}
}
