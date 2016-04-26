package runner.clear;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import api.Action;
import api.GameState;
import api.ObservableGameState;
import runner.experiment.GameTimer;
import utils.GenerateCSV;

public class TraceGameRecord implements GameRecord {
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
	
	public TraceGameRecord(String player1, String player2, String level, String actionSet) throws FileNotFoundException {
		this.id = UUID.randomUUID();
		
		this.player1 = player1;
		this.player2 = player2;
		this.level = level;
		this.actionSet = actionSet;
		this.csv = new GenerateCSV("moves-"+id.toString()+".csv");
		
		this.moves = new HashMap<>();
	}
	
	/* (non-Javadoc)
	 * @see runner.clear.GameRecord#recordAction(int, int, api.Action)
	 */
	@Override
	public void recordAction(int tick, int pid, Action action){
		List<MoveTimer> moveList = moves.get(pid);
		if (moveList == null) {
			moveList = new ArrayList<MoveTimer>();
			moves.put(pid, moveList);
		}
		moveList.add(new MoveTimer(tick, action));
	}
	
	/* (non-Javadoc)
	 * @see runner.clear.GameRecord#recordState(int, api.GameState)
	 */
	@Override
	public void recordState(int tick, ObservableGameState state, Action act0, Action act1) {
		
		csv.writeLine("STATE",
				id,
				player1,
				player2,
				level,
				actionSet,
				tick,
				act0.getFriendlyName(),
				act1.getFriendlyName(),
				state.getPos(0),
				state.getPos(1),
				state.getScore(),
				state.getFlare(0),
				state.getFlare(1),
				state.getSignalState(0),
				state.getSignalState(1),
				state.getSignalState(2),
				state.getSignalState(3),
				state.getSignalState(4),
				state.getSignalState(5),
				state.hasVisited(0, 0),
				state.hasVisited(1, 0),
				GameTimer.getUserTime(),
				System.nanoTime()
				);
		
		
	}
	
	/* (non-Javadoc)
	 * @see runner.clear.GameRecord#recordResult(int, double, runner.clear.Result)
	 */
	@Override
	public void recordResult(int tick, double score, Result result) {
		assert result == null : "strange, result got reported twice...";
		this.result = result;
		this.score = score;
		this.tick = tick;
		System.out.println("game complete: "+result);
	}
	
	/* (non-Javadoc)
	 * @see runner.clear.GameRecord#getResultString()
	 */
	@Override
	public String getResultString() {
		return String.format("%s(%f,%d)", result, score, tick);
	}
	
	public String toString(){
		return String.format("%s - %s & %s (%s,%s)", id, player1, player2, level, actionSet);
	}
	
	static class MoveTimer {
		Action action;
		int tick;
		
		public MoveTimer(int tick, Action action) {
			this.tick = tick;
			this.action = action;
		}
		
		public String toString() {
			return String.format("%d: %s", tick, action);
		}
	}

	@Override
	public String getID() {
		return id.toString();
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
	public String getLevel() {
		return level;
	}

	@Override
	public String getActionSet() {
		return actionSet;
	}

	@Override
	public String getResult() {
		return result.toString();
	}

	@Override
	public double getScore() {
		return score;
	}

	@Override
	public int getTicks() {
		return tick;
	}
	
}
