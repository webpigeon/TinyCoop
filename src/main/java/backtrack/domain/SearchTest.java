package backtrack.domain;

import java.util.List;

import backtrack.State;
import backtrack.Dijkstra;
import backtrack.IDSearch;
import backtrack.Search;

public class SearchTest {

	public static void main(String[] args) {
		Search search = new Dijkstra();
		testGrid(search);
	}
	
	private static void testGrid(Search search) {
		State start = new GridState(0,0);
		State goal = new GridState(0,1);
		List<State> goalResult = search.search(start, goal);
		
		System.out.println(goal);
		System.out.println(goalResult);
	}

}
