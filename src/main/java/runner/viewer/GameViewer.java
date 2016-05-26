package runner.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.swing.JFrame;

import runner.experiment.GameResult;
import runner.experiment.GameSetup;
import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.controller.Controller;
import uk.me.webpigeon.phd.tinycoop.engine.Filters;
import uk.me.webpigeon.phd.tinycoop.engine.SimpleGame;
import uk.me.webpigeon.phd.tinycoop.engine.level.GameLevel;
import uk.me.webpigeon.phd.tinycoop.engine.level.LevelParser;
import utils.AgentFactory;
import utils.GVGAIAgentFactory;
import utils.LegacyAgentFactory;

public class GameViewer implements Callable<GameResult> {
	private static final Integer SLEEP_TIME = 60 / 1;

	public static void main(String[] args) throws Exception {
		// GameViewer viewer = new GameViewer(level, p1, p2);
		// viewer.call();

		String[] maps = new String[] {
				"data/norm_maps/airlock.txt",
				"data/norm_maps/butterfly.txt",
				"data/norm_maps/single_door.txt",
				"data/norm_maps/cloverleaf.txt",
				"data/norm_maps/mirror_lock.txt",
				"data/norm_maps/maze.txt"
		};

		Map<String, List<GameResult>> resultMap = new HashMap<String, List<GameResult>>();

		for (int i = 0; i < 10; i++) {
			for (String map : maps) {
				List<GameResult> results = resultMap.get(map);
				if (results == null) {
					results = new ArrayList<>();
					resultMap.put(map, results);
				}

				GameLevel level = LevelParser.buildParser(map);
				level.setLegalMoves("simple", Filters.getBasicActions());

				// Controller p1 = new WASDController();
				// Controller p2 = new PassiveRefindController();

				// Controller p1 = Utils.buildPredictor(new FollowTheFlare(),
				// "pmcts");

				SimpleGame game = new SimpleGame(level);
				
				Controller p1 = GVGAIAgentFactory.buildMCTS(game, 0);
				Controller p2 = GVGAIAgentFactory.buildMCTS(game, 1);

				GameViewer viewer = new GameViewer(level, game, p1, p2);
				GameResult r = viewer.call();
				results.add(r);
			}
		}

		System.out.println(resultMap);

	}

	private GameLevel level;
	private SimpleGame game;
	private Controller p1;
	private Controller p2;

	public GameViewer(GameLevel level, SimpleGame game, Controller p1, Controller p2) {
		this.level = level;
		this.game = game;
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public GameResult call() throws Exception {

		// setup result entry already
		GameSetup setup = new GameSetup();
		setup.levelID = level.getLevelName();
		setup.actionSet = level.getActionSetName();
		setup.p1 = p1.getFriendlyName();
		setup.p2 = p2.getFriendlyName();

		GameResult result = new GameResult(setup);
;
		p1.startGame(0, 1);
		p2.startGame(1, 0);

		JFrame frame = new JFrame("TinyCoOp - Observable Edition");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Viewer viewer = new Viewer(game);
		frame.add(viewer);

		if (p1 instanceof WASDController) {
			viewer.addMouseListener((WASDController) p1);
			viewer.addKeyListener((WASDController) p1);
			viewer.setFocusable(true);
			viewer.requestFocus();
		}

		frame.pack();
		frame.setVisible(true);

		// make a list of legal moves for each player
		List<Action> legalMoves1 = game.getLegalActions(0);
		List<Action> legalMoves2 = game.getLegalActions(1);

		int tickCount = 0;
		while (!game.hasWon()) {
			Action p1Move = p1.getAction(game.getObservationFor(0));
			Action p2Move = p2.getAction(game.getObservationFor(1));

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
