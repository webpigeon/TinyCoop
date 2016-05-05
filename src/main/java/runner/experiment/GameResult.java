package runner.experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import api.Action;

public class GameResult {

	static class MovePair {
		int tick;
		Action p1;
		Action p2;

		MovePair(int tick, Action p1, Action p2) {
			this.tick = tick;
			this.p1 = p1;
			this.p2 = p2;
		}
	}

	public final GameSetup setup;

	public UUID id;
	public double score;
	public int ticks;
	public int disquals = 0;
	public List<MovePair> moves;
	public float userTime;

	public float wallTime;

	public List<GameTick> trace;

	public GameResult(GameSetup setup) {
		this.setup = setup;
		this.moves = new ArrayList<MovePair>();
		this.trace = new ArrayList<GameTick>();
	}

	public void recordMoves(int tickCount, Action p1Move, Action p2Move) {
		moves.add(new MovePair(tickCount, p1Move, p2Move));
	}

	@Override
	public String toString() {
		return String.format("(%s): %f %d ", setup.toString(), score, ticks);
	}
}
