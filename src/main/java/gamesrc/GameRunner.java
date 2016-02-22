package gamesrc;

import java.io.IOException;

import javax.swing.JFrame;

import Controllers.Controller;
import Controllers.RandomController;
import utils.StatSummary;

public class GameRunner {

	public static void main(String[] args) throws IOException {
		GameLevel level = LevelParser.buildParser("data/maps/level1.txt");
		SimpleGame game = new SimpleGame(level);

		Viewer viewer = new Viewer(game);
		JFrame frame = new JFrame("Tiny CoOp - Observerable Edition");
		frame.add(viewer);
		frame.pack();
		frame.setVisible(true);
		
		Controller p1 = new RandomController();
		Controller p2 = new RandomController();
		
		
		StatSummary scores = new StatSummary();
		StatSummary ticks = new StatSummary();
		
		for (int i=0; i<10; i++) {
			
			int tickCount = 0;
			while (!game.hasWon()) {
				game.update(p1.get(game.getClone()), p2.get(game.getClone()));
				frame.repaint();
				tickCount++;
			}
			
			scores.add(game.getScore());
			ticks.add(tickCount);
		}
		
		System.out.println("result: "+scores.mean()+" "+ticks.mean());
	}

}
