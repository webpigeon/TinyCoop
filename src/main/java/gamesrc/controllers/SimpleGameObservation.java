package gamesrc.controllers;

import java.awt.Point;
import java.util.List;

import api.Action;
import api.Flare;
import api.GameObject;
import api.GameState;
import api.controller.GameObservation;
import gamesrc.SimpleGame;

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
	public GameObject getObject(int x, int y) {
		return state.getObject(x, y);
	}

	@Override
	public Point getPos(int agent) {
		return state.getPos(agent);
	}

	@Override
	public int getSignalState(int signal) {
		return state.getSignalState(signal);
	}

	@Override
	public boolean hasVisited(int agent, int goalID) {
		return state.hasVisited(agent, goalID);
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
	public int getActionLength() {
		return state.getActionLength();
	}

	@Override
	public GameState getClone() {
		return state.getClone();
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
	public double getScore() {
		return state.getScore();
	}

	@Override
	public int getWidth() {
		return state.getWidth();
	}

	@Override
	public boolean hasWon() {
		return state.hasWon();
	}

	@Override
	public void update(Action p1, Action p2) {
		state.update(p1, p2);
	}

	@Override
	public GameObservation simulate(Action ours, Action theirs) {
		SimpleGame newState = (SimpleGame)state.getClone();
		
		if (playerID == GameState.PLAYER_0) {
			newState.update(ours, theirs);
		} else {
			newState.update(theirs, ours);
		}
		
		return new SimpleGameObservation(newState, playerID);
	}

	@Override
	public GameObservation getCopy() {
		SimpleGame newState = (SimpleGame)state.getClone();
		return new SimpleGameObservation(newState, playerID);
	}

	@Override
	public void apply(Action ours, Action theirs) {
		if (playerID == GameState.PLAYER_0) {
			state.update(ours, theirs);
		} else {
			state.update(theirs, ours);
		}
	}

}
