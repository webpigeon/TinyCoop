package uk.me.webpigeon.phd.search;

import java.util.List;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.GameState;

public interface Search {

	public List<Action> search(GameState start, GameState end);

}
