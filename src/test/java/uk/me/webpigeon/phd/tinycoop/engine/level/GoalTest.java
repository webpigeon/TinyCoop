package uk.me.webpigeon.phd.tinycoop.engine.level;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import FastGame.ObjectTypes;
import uk.me.webpigeon.phd.tinycoop.api.GameState;
import uk.me.webpigeon.phd.tinycoop.engine.SimpleGame;
import uk.me.webpigeon.phd.tinycoop.engine.level.Goal;

public class GoalTest {
	private int goalId = 1;
	private Goal goal;

	@Before
	public void setUp() throws Exception {
		this.goal = new Goal(goalId);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSignal() {
		assertEquals(goalId, goal.getSignal());
	}

	@Test
	public void testGetType() {
		assertEquals(ObjectType.GOAL, goal.getType());
	}

	@Test
	public void testOnContact() {
		SimpleGame state = mock(SimpleGame.class);

		int agentID = GameState.PLAYER_0;
		int otherAgentID = GameState.PLAYER_1;

		goal.onContact(state, agentID);
		verify(state).setVisited(agentID, goalId);
		verify(state, never()).setVisited(otherAgentID, goalId);
	}

}
