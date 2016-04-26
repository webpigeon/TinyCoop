package gamesrc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import api.Action;
import api.GameState;
import gamesrc.level.GameLevel;

public class SimpleGameTest {
	private static final Double EPSILON = 0.000001;
	private GameLevel mockLevel;
	private SimpleGame instance;

	@Before
	public void setUp() throws Exception {
		Action actionMock = mock(Action.class);
		
		mockLevel = mock(GameLevel.class);
		stub(mockLevel.getPlayerCount()).toReturn(2);
		stub(mockLevel.getGoalCount()).toReturn(1);
		stub(mockLevel.getLegalMoves()).toReturn(Arrays.asList(actionMock));
		
		this.instance = new SimpleGame(mockLevel);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInitialScoreIsZero() {
		double expected = 0;
		double result = instance.getScore();
		
		assertEquals(expected, result, EPSILON);
	}
	
	@Test
	public void testInitialScoreAfterVisitIsOne() {
		double expected = 1;
		
		instance.setVisited(GameState.PLAYER_0, 0);
		instance.setVisited(GameState.PLAYER_1, 0);
		
		double result = instance.getScore();
		
		assertEquals(expected, result, EPSILON);
	}

	@Test
	public void testInitialHasWon() {
		boolean expected = false;
		boolean result = instance.hasWon();
		
		assertEquals(expected, result);
	}

	@Test
	public void testGoalContract() {
		int agent = 0;
		int goal = 0;
		
		instance.setVisited(agent, goal);
		
		boolean expected = true;
		boolean result = instance.hasVisited(agent, goal);
		assertEquals(expected, result);
	}

	@Test
	public void testGetGoalsCount() {
		int expected = 1;
		int result = instance.getGoalsCount();
		assertEquals(expected, result);
	}

	@Test
	public void testGetLegalActionsIsImmutable() {
		Action[] a1 = instance.getLegalActions(0);
		Action[] a2 = instance.getLegalActions(0);
		
		a1[0] = null;
		assertNotEquals(a1[0], a2[0]);
	}
	
	@Test
	public void testSignalInitiallyZero() {
		int signal = 0;
		int expected = 0;
		int result = instance.getSignalState(signal);
		
		assertEquals(expected, result);
	}
	
	@Test
	public void testSignalIncrease() {
		int signal = 0;
		instance.setSignalState(signal, true);
		
		int expected = 1;
		int result = instance.getSignalState(signal);
		assertEquals(expected, result);
		assertEquals(true, instance.isSignalHigh(signal));
	}
	
	@Test
	public void testSignalDecrease() {
		int signal = 0;
		instance.setSignalState(signal, false);
		
		int expected = -1;
		int result = instance.getSignalState(signal);
		assertEquals(expected, result);
		assertEquals(false, instance.isSignalHigh(signal));
	}
	
	@Test
	public void testSignalDecreaseAfterIncrease() {
		int signal = 0;
		instance.setSignalState(signal, true);
		instance.setSignalState(signal, false);
		
		int expected = 0;
		int result = instance.getSignalState(signal);
		assertEquals(expected, result);
		assertEquals(false, instance.isSignalHigh(signal));
	}

}
