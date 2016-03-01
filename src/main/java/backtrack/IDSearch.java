package backtrack;

import java.util.List;

public class IDSearch implements Search {
	private int maxDepth;
	
	
	public IDSearch(int maxDepth){
		this.maxDepth = maxDepth;
	}

	public List<State> search(State start, State end) {
		
		for (int i=0; i<maxDepth; i++) {
			Search dfs = new DepthLimitedDFS(i);
			List<State> path = dfs.search(start, end);
			if (path != null) {
				return path;
			}
		}
		
		return null;
	}


}
