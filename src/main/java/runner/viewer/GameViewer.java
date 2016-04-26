package runner.viewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.swing.JFrame;

import Controllers.Controller;
import Controllers.FollowTheFlare;
import Controllers.PassiveRefindController;
import Controllers.RandomController;
import api.Action;
import gamesrc.Filters;
import gamesrc.SimpleGame;

import gamesrc.level.GameLevel;
import gamesrc.level.LevelParser;
import runner.experiment.GameResult;
import runner.experiment.GameSetup;
import runner.experiment.Utils;

public class GameViewer implements Callable<GameResult> {
	private static final Integer SLEEP_TIME = 60/1;
	
	public static void main(String[] args) throws Exception {
		//GameViewer viewer = new GameViewer(level, p1, p2);
		//viewer.call();

		String[] maps = new String[] {
				//"data/norm_maps/airlock.txt",
				//"data/norm_maps/butterfly.txt",
				"data/norm_maps/single_door.txt",
				//"data/norm_maps/empty.txt",
				//"data/norm_maps/mirror_lock.txt",
				//"data/norm_maps/maze.txt"
		};
		
		Map<String, List<GameResult>> resultMap = new HashMap<String, List<GameResult>>();
		
		for (int i=0; i<10; i++) {
			for (String map : maps) {
				List<GameResult> results = resultMap.get(map);
				if (results == null) {
					results = new ArrayList<>();
					resultMap.put(map, results);
				}
				
				GameLevel level = LevelParser.buildParser(map);
				level.setLegalMoves("simple", Filters.getAllRelativeActions());
				
				//Controller p1 = new WASDController();
				//Controller p2 = new PassiveRefindController();
				
				Controller p1 = new UCBGreedyRollout();
				//Controller p1 = Utils.buildPredictor(new FollowTheFlare());
				//Controller p2 = new RandomController();
				Controller p2 = new GreedyRollout();
				
				
				GameViewer viewer = new GameViewer(level, p1, p2);
				GameResult r = viewer.call();
				results.add(r);
			}
		}
		
		System.out.println(resultMap);

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
