package backtrack;

import java.util.List;

import api.Action;
import api.GameState;

public class IDSearch implements Search {
	private int maxDepth;
	
	
	public IDSearch(int maxDepth){
		this.maxDepth = maxDepth;
	}

	@Override
	public List<Action> search(GameState start, GameState end) {
		
		for (int i=0; i<maxDepth; i++) {
			Search dfs = new DepthLimitedDFS(i);
			List<Action> path = dfs.search(start, end);
			if (path != null) {
				return path;
			}
		}
		
		return null;
	}


}
