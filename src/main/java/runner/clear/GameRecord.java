package runner.clear;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.ObservableGameState;

public interface GameRecord {

	void gameStarted();

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