package gamesrc;

import java.io.IOException;

import javax.swing.JFrame;

import Controllers.Controller;
import Controllers.MCTS;
import Controllers.PassiveController;
import Controllers.RandomController;
import Controllers.SortOfRandomController;
import Controllers.enhanced.NestedControllerPredictor;
import Controllers.enhanced.Predictor;
import Controllers.enhanced.PredictorMCTS;
import utils.StatSummary;

public class GameRunner {

	public static void main(String[] args) throws IOException {
		GameLevel level = LevelParser.buildParser("data/maps/level1.txt");
		SimpleGame gameClean = new SimpleGame(level);

		Viewer viewer = new Viewer(gameClean);
		JFrame frame = new JFrame("Tiny CoOp - Observerable Edition");
		frame.add(viewer);
		frame.pack();
		frame.setVisible(true);
		
		Controller predictorController = new PassiveController();
		Predictor predictor = new NestedControllerPredictor(predictorController);
		Controller p1 = new PredictorMCTS(true, 5000, 1000, 1000, predictor);
		
		
		Controller p2 = new PassiveController();
		
		
		StatSummary scores = new StatSummary();
		StatSummary ticks = new StatSummary();
		
		long lastTime = System.nanoTime();
		for (int i=0; i<1000; i++) {
			ObservableGameState game = (ObservableGameState)gameClean.getClone();
			p1.startGame(0);
			p2.startGame(1);
			viewer.setState(game);
			
			int tickCount = 0;
			while (!game.hasWon()) {
				Action p1Move = p1.get(game.getClone());
				Action p2Move = p2.get(game.getClone());
				
				game.update(p1Move, p2Move);
				frame.repaint();
				tickCount++;
				
				try {
					//Thread.sleep(1000);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				if(tickCount % 100 == 0) {
					long time = System.nanoTime();
					//System.out.println("tickTimer: "+(time - lastTime) / 100f);
					lastTime = time;
				}
				
				//System.out.println(p1Move + " " + p2Move);
				//System.out.println("tick "+System.currentTimeMillis());
			}
			
			scores.add(game.getScore());
			ticks.add(tickCount);
		}
		
		System.out.println("result: "+scores.mean()+" "+ticks.mean());
	}

}
