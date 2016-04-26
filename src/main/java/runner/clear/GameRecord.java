package runner.clear;

import api.Action;
import api.GameState;
import api.ObservableGameState;

public interface GameRecord {

	String getID();
	String getPlayer1();
	String getPlayer2();
	String getLevel();
	String getActionSet();
	String getResult();
	double getScore();
	int getTicks();
	
	void recordAction(int tick, int pid, Action action);

	void recordState(int tick, ObservableGameState state, Action act0, Action act1);

	void recordResult(int tick, double score, Result result);

	String getResultString();

}