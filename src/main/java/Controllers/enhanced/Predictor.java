package Controllers.enhanced;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.GameState;

public interface Predictor {

	public void init(int agentID);

	public void observe(int pid, Action... history);

	public Action predict(int pid, GameState state);

}
