package gamesrc.level;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import FastGame.ObjectTypes;
import api.GameState;
import api.ObservableGameState;

public class DoorTest {
	private static final Integer DOOR_SIGNAL = 1;
	private Door door;

	@Before
	public void setUp() throws Exception {
		this.door = new Door(DOOR_SIGNAL);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSignal() {
		assertEquals((int) DOOR_SIGNAL, door.getSignal());
	}

	@Test
	public void testGetType() {
		assertEquals(ObjectTypes.DOOR, door.getType());
	}

	@Test
	public void testIsNotWalkablePlayerOne() {
		ObservableGameState state = mock(ObservableGameState.class);
		when(state.isSignalHigh(DOOR_SIGNAL)).thenReturn(false);

		boolean expected = false;
		boolean result = door.isWalkable(state, GameState.PLAYER_1);
		assertEquals(expected, result);
	}

	@Test
	public void testIsNotWalkablePlayerZero() {
		ObservableGameState state = mock(ObservableGameState.class);
		when(state.isSignalHigh(DOOR_SIGNAL)).thenReturn(false);

		boolean expected = false;
		boolean result = door.isWalkable(state, GameState.PLAYER_0);
		assertEquals(expected, result);
	}

	@Test
	public void testIsWalkablePlayerOne() {
		ObservableGameState state = mock(ObservableGameState.class);
		when(state.isSignalHigh(DOOR_SIGNAL)).thenReturn(true);

		boolean expected = true;
		boolean result = door.isWalkable(state, GameState.PLAYER_1);
		assertEquals(expected, result);
	}

	@Test
	public void testIsWalkablePlayerZero() {
		ObservableGameState state = mock(ObservableGameState.class);
		when(state.isSignalHigh(DOOR_SIGNAL)).thenReturn(true);

		boolean expected = true;
		boolean result = door.isWalkable(state, GameState.PLAYER_0);
		assertEquals(expected, result);
	}

}
