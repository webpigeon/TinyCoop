package gamesrc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import FastGame.Action;
import FastGame.TalkAction;

public class Filters {

	/**
	 * Filter all actions and leave only movement actions
	 * 
	 * @param sourceActions
	 * @return
	 */
	public static List<Action> filterMovement(Action[] sourceActions){
		List<Action> l = new ArrayList<Action>();
		for (Action action : sourceActions) {
			if(action.isMovement()) {
				l.add(action);
			}
		}
		return l;
	}
	
	/**
	 * Filter all actions and leave only communication actions
	 * 
	 * @param sourceActions
	 * @return
	 */
	public static List<Action> filterTalk(Action[] sourceActions){
		List<Action> l = new ArrayList<Action>();
		for (Action action : sourceActions) {
			if(action.isTalk()) {
				l.add(action);
			}
		}
		return l;
	}
	
	/**
	 * Remove some actions which are impossible.
	 * ie. movement and flares which target non-walkable squares.
	 * 
	 * @param pid
	 * @param state
	 * @param sourceActions
	 * @return
	 */
	public static List<Action> filterImpossible(int pid, ObservableGameState state, Action[] sourceActions) {
		List<Action> l = new ArrayList<Action>();
		for (Action action : sourceActions) {
			if(state.isWalkable(pid, action.getX(), action.getY())) {
				l.add(action);
			}
		}
		return l;
	}
	
	public static List<Action> getAllActions(int width, int height) {

		List<Action> legalActions = new ArrayList<Action>();
	
		legalActions.addAll(getBasicActions());
		legalActions.addAll(generateFlares(width, height));
		
		return legalActions;
	}
	
	public static List<Action> getAllRelativeActions() {

		List<Action> legalActions = new ArrayList<Action>();
	
		legalActions.addAll(getBasicActions());
		legalActions.addAll(generateRelativeFlares());
		
		return legalActions;
	}
	
	
	public static List<Action> getBasicActions() {
		return Arrays.asList(Action.NOOP, Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT);
	}
	
	public static List<Action> getBasicActionsWithBeep() {
		List<Action> legalActions = new ArrayList<Action>(getBasicActions());
		legalActions.add(Action.BEEP);
		return legalActions;
	}
	
	public static List<Action> generateFlares(int width, int height) {
		List<Action> legalActions = new ArrayList<Action>();
		//generate full grid talk actions
		for (int x = 0; x < width; x++) {
			for (int y=0; y < height; y++) {
				legalActions.add(new TalkAction(x, y, false));
			}
		}
		return legalActions;
	}
	
	public static List<Action> generateRelativeFlares() {
		return Arrays.asList(TalkAction.FLARE_DOWN, TalkAction.FLARE_UP, TalkAction.FLARE_LEFT, TalkAction.FLARE_RIGHT);
	}
	
}
