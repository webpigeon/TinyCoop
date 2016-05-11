package Controllers.planning;

import api.GameState;

public interface Heuristic {

	public double getScore(GameState state);

	public void init();

}
