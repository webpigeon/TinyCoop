package backtrack;

import java.util.List;

import actions.Action;
import gamesrc.GameState;

public interface Search {

	public List<Action> search(GameState start, GameState end);
	
}
