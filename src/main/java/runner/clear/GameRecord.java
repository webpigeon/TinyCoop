package runner.clear;

import api.Action;
import api.ObservableGameState;

public interface GameRecord {

	String getActionSet();

	String getID();

	String getLevel();

	String getPlayer1();

	String getPlayer2();

	String getResult();

	String getResultString();

	double getScore();

	int getTicks();

	void recordAction(int tick, int pid, Action action);

	void recordResult(int tick, double score, Result result);

	void recordState(int tick, ObservableGameState state, Action act0, Action act1);

}