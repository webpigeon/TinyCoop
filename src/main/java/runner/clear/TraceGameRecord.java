package runner.clear;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import api.Action;
import api.ObservableGameState;
import runner.experiment.GameTimer;
import utils.GenerateCSV;

public class TraceGameRecord implements GameRecord {
	static class MoveTimer {
		Action action;
		int tick;

		public MoveTimer(int tick, Action action) {
			this.tick = tick;
			this.action = action;
		}

		@Override
		public String toString() {
			return String.format("%d: %s", tick, action);
		}
	}

	private final Map<Integer, List<MoveTimer>> moves;
	private final UUID id;
	private final String player1;
	private final String player2;
	private final String level;

	private final String actionSet;
	private Result result;
	private double score;

	private int tick;

	private GenerateCSV csv;

	public TraceGameRecord(GameSetup setup) throws FileNotFoundException {
		this(setup.getPlayer0Name(), setup.getPlayer1Name(), setup.getLevelName(), setup.getActionSetName());
	}

	public TraceGameRecord(String player1, String player2, String level, String actionSet)
			throws FileNotFoundException {
		this.id = UUID.randomUUID();

		this.player1 = player1;
		this.player2 = player2;
		this.level = level;
		this.actionSet = actionSet;


		this.moves = new HashMap<>();
	}

	@Override
	public String getActionSet() {
		return actionSet;
	}

	@Override
	public String getID() {
		return id.toString();
	}

	@Override
	public String getLevel() {
		return level;
	}

	@Override
	public String getPlayer1() {
		return player1;
	}

	@Override
	public String getPlayer2() {
		return player2;
	}

	@Override
	public String getResult() {
		return result.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see runner.clear.GameRecord#getResultString()
	 */
	@Override
	public String getResultString() {
		return String.format("%s(%f,%d)", result, score, tick);
	}

	@Override
	public double getScore() {
		return score;
	}

	@Override
	public int getTicks() {
		return tick;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see runner.clear.GameRecord#recordAction(int, int, api.Action)
	 */
	@Override
	public void recordAction(int tick, int pid, Action action) {
		List<MoveTimer> moveList = moves.get(pid);
		if (moveList == null) {
			moveList = new ArrayList<MoveTimer>();
			moves.put(pid, moveList);
		}
		moveList.add(new MoveTimer(tick, action));
		//System.out.println("move made: "+tick+" "+pid+" "+action);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see runner.clear.GameRecord#recordResult(int, double,
	 * runner.clear.Result)
	 */
	@Override
	public void recordResult(int tick, double score, Result result) {
		assert result == null : "strange, result got reported twice...";
		this.result = result;
		this.score = score;
		this.tick = tick;
		System.out.println("game complete: "+ tick + " " + result+" "+player1+" "+player2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see runner.clear.GameRecord#recordState(int, api.GameState)
	 */
	@Override
	public void recordState(int tick, ObservableGameState state, Action act0, Action act1) {
		//System.out.println(tick+" state recorded "+act0+" "+act1);
		
		try {
		csv.writeLine("STATE", id, player1, player2, level, actionSet, tick, act0.getFriendlyName(),
				act1.getFriendlyName(), state.getPos(0), state.getPos(1), state.getScore(), state.getFlare(0),
				state.getFlare(1), state.getSignalState(0), state.getSignalState(1), state.getSignalState(2),
				state.getSignalState(3), state.getSignalState(4), state.hasVisited(0, 0),
				state.hasVisited(1, 0), GameTimer.getUserTime(), System.nanoTime());
		} catch (Exception ex) {
			System.err.println(String.format("error: could not write to log: [%d] %s, %s", tick, act0, act1));
			ex.printStackTrace();
		}

	}

	@Override
	public String toString() {
		return String.format("%s - %s & %s (%s,%s)", id, player1, player2, level, actionSet);
	}

	@Override
	public void gameStarted() {
		try {
			File moveDir = new File("moves/");
			moveDir.mkdirs();
			
			File moveFile = new File(moveDir, "moves-" + id.toString() + ".csv");
			this.csv = new GenerateCSV(moveFile);
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

}
