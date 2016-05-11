package runner.tinycoop;

import runner.clear.Result;

public class GameResult {
	public final GameSetup setup;
	public final Result result;
	public final Double score;
	public final Integer ticks;
	public final Long wallTime;
	public final Long cpuTime;

	public GameResult(GameSetup setup, Result result, double score, int ticks, long wallTime, long cpuTime) {
		this.setup = setup;
		this.result = result;
		this.score = score;
		this.ticks = ticks;
		this.wallTime = wallTime;
		this.cpuTime = cpuTime;
	}

	@Override
	public String toString() {
		return String.format("%s, %f in %d ticks", result, score, ticks);
	}

}
