package Controllers.planning;

import uk.me.webpigeon.phd.tinycoop.api.GameState;

public interface Heuristic {

	public double getScore(GameState state);

	public void init();

}
