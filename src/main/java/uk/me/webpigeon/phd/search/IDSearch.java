package uk.me.webpigeon.phd.search;

import java.util.List;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.GameState;

public class IDSearch implements Search {
	private int maxDepth;

	public IDSearch(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	@Override
	public List<Action> search(GameState start, GameState end) {

		for (int i = 0; i < maxDepth; i++) {
			Search dfs = new DepthLimitedDFS(i);
			List<Action> path = dfs.search(start, end);
			if (path != null) {
				return path;
			}
		}

		return null;
	}

}
