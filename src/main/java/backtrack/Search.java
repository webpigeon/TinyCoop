package backtrack;

import java.util.List;

import api.Action;
import api.GameState;

public interface Search {

	public List<Action> search(GameState start, GameState end);

}
