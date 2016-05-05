package Controllers.enhanced;

import api.Action;
import api.GameState;

public interface Predictor {

	public void init(int agentID);

	public void observe(int pid, Action... history);

	public Action predict(int pid, GameState state);

}
