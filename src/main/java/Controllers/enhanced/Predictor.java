package Controllers.enhanced;

import actions.Action;
import gamesrc.GameState;

public interface Predictor {
	
	public void init(int agentID);
	public void observe(int pid, Action ... history);
	public Action predict(int pid, GameState state);

}
