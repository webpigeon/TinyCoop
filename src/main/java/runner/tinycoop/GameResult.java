package runner.tinycoop;

import runner.clear.Result;

public class GameResult {
	public final Result result;
	public final Double score;
	public final Integer ticks;

	public GameResult(Result result, double score, int ticks) {
		this.result = result;
		this.score = score;
		this.ticks = ticks;
	}

	@Override
	public String toString() {
		return String.format("%s, %f in %d ticks", result, score, ticks);
	}

}
