package uk.me.webpigeon.phd.tinycoop.engine.controllers;

import java.awt.Point;
import java.util.List;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.Flare;
import uk.me.webpigeon.phd.tinycoop.api.GameObject;
import uk.me.webpigeon.phd.tinycoop.api.GameState;
import uk.me.webpigeon.phd.tinycoop.api.controller.GameObservation;
import uk.me.webpigeon.phd.tinycoop.engine.SimpleGame;

/**
 * New style controllers get one of these rather than the game directly.
 */
public class SimpleGameObservation implements GameObservation {
	private final SimpleGame state;
	private final Integer playerID;

	public SimpleGameObservation(SimpleGame state, int playerID) {
		this.state = state;
		this.playerID = playerID;
	}

	@Override
	public void apply(Action ours, Action theirs) {
		if (playerID.equals(GameState.PLAYER_0)) {
			state.update(ours, theirs);
		} else {
			state.update(theirs, ours);
		}
	}

	@Override
	public int getActionLength() {
		return state.getActionLength();
	}

	@Override
	public GameState getClone() {
		return state.getClone();
	}

	@Override
	public GameObservation getCopy() {
		SimpleGame newState = state.getClone();
		return new SimpleGameObservation(newState, playerID);
	}

	@Override
	public Flare getFlare(int agent) {
		return state.getFlare(agent);
	}

	@Override
	public int getFloor(int x, int y) {
		return state.getFloor(x, y);
	}

	@Override
	public int getGoalsCount() {
		return state.getGoalsCount();
	}

	@Override
	public int getHeight() {
		return state.getHeight();
	}

	@Override
	public List<Action> getLegalActions(int playerID) {
		return state.getLegalActions(playerID);
	}

	@Override
	public GameObject getObject(int x, int y) {
		return state.getObject(x, y);
	}

	@Override
	public Point getPos(int agent) {
		return state.getPos(agent);
	}

	@Override
	public double getScore() {
		return state.getScore();
	}

	@Override
	public int getSignalState(int signal) {
		return state.getSignalState(signal);
	}

	@Override
	public int getWidth() {
		return state.getWidth();
	}

	@Override
	public boolean hasVisited(int agent, int goalID) {
		return state.hasVisited(agent, goalID);
	}

	@Override
	public boolean hasWon() {
		return state.hasWon();
	}

	@Override
	public boolean isSignalHigh(int signal) {
		return state.isSignalHigh(signal);
	}

	@Override
	public boolean isWalkable(int pid, int x, int y) {
		return state.isWalkable(pid, x, y);
	}

	@Override
	public GameObservation simulate(Action ours, Action theirs) {
		SimpleGame newState = state.getClone();

		if (playerID.equals(GameState.PLAYER_0)) {
			newState.update(ours, theirs);
		} else {
			newState.update(theirs, ours);
		}

		return new SimpleGameObservation(newState, playerID);
	}

	@Override
	public void update(Action p1, Action p2) {
		state.update(p1, p2);
	}

	@Override
	public GameObservation fromPerspective(int newAgent) {
		return new SimpleGameObservation(state.getClone(), newAgent);
	}
	
	public String toString() {
		return String.format("OBS: %d %s", playerID, state);
	}

}
