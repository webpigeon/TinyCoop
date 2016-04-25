package gamesrc.viewer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.swing.JFrame;

import Controllers.Controller;
import Controllers.PassiveRefindController;
import Controllers.WASDController;
import actions.Action;
import gamesrc.Filters;
import gamesrc.GameLevel;
import gamesrc.LevelParser;
import gamesrc.SimpleGame;
import gamesrc.experiment.GameResult;
import gamesrc.experiment.GameSetup;
import gamesrc.experiment.Utils;

public class GameViewer implements Callable<GameResult> {
	private static final Integer SLEEP_TIME = 60/1;
	
	public static void main(String[] args) throws Exception {
		//GameViewer viewer = new GameViewer(level, p1, p2);
		//viewer.call();

		String[] maps = new String[] {
				"data/norm_maps/maze.txt",
				//"data/norm_maps/airlock.txt",
				//"data/norm_maps/butterfly.txt",
				//"data/norm_maps/single_door.txt"
				//"data/norm_maps/empty.txt"
		};
		
		Map<String, GameResult> results = new HashMap<String, GameResult>();
		
		for (String map : maps) {
			GameLevel level = LevelParser.buildParser(map);
			level.setLegalMoves("simple", Filters.getAllRelativeActions());
			
			//Controller p1 = new WASDController();
			//Controller p2 = new PassiveRefindController();
			
			Controller p1 = Utils.buildPredictor(new PassiveRefindController());
			Controller p2 = new PassiveRefindController();
			
			
			GameViewer viewer = new GameViewer(level, p1, p2);
			GameResult r = viewer.call();
			results.put(map, r);
		}
		
		System.out.println(results);

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
		
		if (p1 instanceof WASDController) {
			viewer.addMouseListener((WASDController)p1);
			viewer.addKeyListener((WASDController)p1);
			viewer.setFocusable(true);
			viewer.requestFocus();
		}
		
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
			result.recordMoves(tickCount, p1Move, p2Move);
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
