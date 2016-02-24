package gamesrc;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.JFrame;

import Controllers.Controller;
import Controllers.MCTS;
import Controllers.PassiveRefindController;
import Controllers.enhanced.NestedControllerPredictor;
import Controllers.enhanced.PredictorMCTS;
import FastGame.Action;

public class GameViewer implements Callable<GameResult> {
	private static final Integer SLEEP_TIME = 5;
	
	public static void main(String[] args) throws Exception {
		GameLevel level = LevelParser.buildParser("data/maps/level1.txt");
		level.setLegalMoves("relative", Filters.getAllRelativeActions());
		
		
		NestedControllerPredictor predictor = new NestedControllerPredictor(new PassiveRefindController());
		Controller p1 = new PredictorMCTS(true, 5000, 100, 450, predictor);
		Controller p2 = new PassiveRefindController();
		GameViewer viewer = new GameViewer(level, p1, p2);
		
		viewer.call();

	}

	private GameLevel level;
	private Controller p1;
	private Controller p2;

	public GameViewer(GameLevel level, Controller p1, Controller p2) {
		this.level = level;
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public GameResult call() throws Exception {
				
		// setup result entry already
		GameSetup setup = new GameSetup();
		setup.levelID = level.getLevelName();
		setup.actionSet = level.getActionSetName();
		setup.p1 = p1.getSimpleName();
		setup.p2 = p2.getSimpleName();
		
		GameResult result = new GameResult(setup);

		SimpleGame game = new SimpleGame(level);
		p1.startGame(0);
		p2.startGame(1);
		
		JFrame frame = new JFrame("TinyCoOp - Observable Edition");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Viewer viewer = new Viewer(game);
		frame.add(viewer);
		
		frame.pack();
		frame.setVisible(true);

		// make a list of legal moves for each player
		List<Action> legalMoves1 = Arrays.asList(game.getLegalActions(0));
		List<Action> legalMoves2 = Arrays.asList(game.getLegalActions(1));

		int tickCount = 0;
		while (!game.hasWon()) {
			Action p1Move = p1.get(game.getClone());
			Action p2Move = p2.get(game.getClone());

			if (!legalMoves1.contains(p1Move) || !legalMoves2.contains(p2Move)) {
				System.err.println("illegal move detected " + p1Move + " " + p2Move);
				result.disquals++;
				break;
			}

			game.update(p1Move, p2Move);
			result.recordMoves(p1Move, p2Move);
			viewer.repaint();
			Thread.sleep(SLEEP_TIME);
			
			System.out.printf("%d: %s && %s\n", tickCount, p1Move, p2Move);
			
			tickCount++;
		}

		// record results{
		if (result.disquals == 0) {
			result.score = game.getScore();
			result.ticks = tickCount;
		}

		return result;
	}

}
