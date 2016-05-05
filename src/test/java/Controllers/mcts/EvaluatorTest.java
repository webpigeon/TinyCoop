package Controllers.mcts;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class EvaluatorTest {
	private static final Double EPSILON = 1e-6;

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{1, 1, 1, 1},
			{0, 1, 2, 0},
			{1, 2, 2, 0.5},
		});
	}
	
	private int wins;
	private int visits;
	private int totalVisits;
	private double expectedScore;
	
	public EvaluatorTest(int wins, int visits, int totalVisits, double score) {
		this.wins = wins;
		this.visits = visits;
		this.totalVisits = totalVisits;
		this.expectedScore = score;
	}
	
	@Test
	public void testUCT() {
		assertEquals(expectedScore, Evaluator.UCT(wins, visits, totalVisits), EPSILON);
	}

}
