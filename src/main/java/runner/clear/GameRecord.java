package runner.clear;

import api.Action;
import api.GameState;

public interface GameRecord {

	void recordAction(int tick, int pid, Action action);

	void recordState(int tick, GameState state);

	void recordResult(int tick, double score, Result result);

	String getResultString();

}