package Controllers.planning;

import api.GameState;

public interface Heuristic {
	
	public void init();
	
	public double getScore(GameState state);


}
