package runner.clear;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import api.Action;
import api.GameState;
import api.ObservableGameState;
import runner.tinycoop.GameSetup;

public class MemoryGameRecord implements GameRecord {
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

	private final Map<Integer, GameState> states;
	private final UUID id;
	private final String player1;
	private final String player2;
	private final String level;

	private final String actionSet;
	private Result result;
	private double score;

	private int tick;

	public MemoryGameRecord(GameSetup setup) {
		this(setup.getPlayer0Name(), setup.getPlayer1Name(), setup.getLevelName(), setup.getActionSetName());
	}

	public MemoryGameRecord(String player1, String player2, String level, String actionSet) {
		this.id = UUID.randomUUID();

		this.player1 = player1;
		this.player2 = player2;
		this.level = level;
		this.actionSet = actionSet;

		this.moves = new HashMap<>();
		this.states = new HashMap<>();
	}

	@Override
	public void gameStarted() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getActionSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLevel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlayer1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlayer2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTicks() {
		// TODO Auto-generated method stub
		return 0;
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
		System.out.println("game complete: " + result);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see runner.clear.GameRecord#recordState(int, api.GameState)
	 */
	@Override
	public void recordState(int tick, ObservableGameState state, Action p0, Action p1) {
		states.put(tick, state);
		recordAction(tick, 0, p0);
		recordAction(tick, 1, p1);
	}

	@Override
	public String toString() {
		return String.format("%s - %s & %s (%s,%s)", id, player1, player2, level, actionSet);
	}

}
