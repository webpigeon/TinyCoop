package gamesrc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import actions.absolute.AbsoluteFlare;
import api.Action;
import api.ActionType;

public class Filters {

	/**
	 * Filter all actions and leave only movement actions
	 * 
	 * @param sourceActions
	 * @return
	 */
	public static List<Action> filterMovement(Action[] sourceActions){
		return filterSet(sourceActions, ActionType.MOVEMENT);
	}
	
	/**
	 * Filter all actions and leave only communication actions
	 * 
	 * @param sourceActions
	 * @return
	 */
	public static List<Action> filterTalk(Action[] sourceActions){
		return filterSet(sourceActions, ActionType.FLARE);
	}
	
	
	public static List<Action> filterSet(Action[] src, ActionType ... types) {
		List<Action> filtered = new ArrayList<Action>();
		
		for (Action action : src) {
			if (matches(action.getType(), types)) {
				filtered.add(action);
			}
		}
		
		return filtered;
	}
	
	private static boolean matches(ActionType type, ActionType ... actionTypes ){
		if (type == null) return false;
		for (ActionType other : actionTypes) {
			if (type.equals(other)) {
				return true;
			}
		}
		return false;
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
		return Arrays.asList(Action.NOOP, Action.MOVE_UP, Action.MOVE_DOWN, Action.MOVE_LEFT, Action.MOVE_RIGHT);
	}
	
	public static List<Action> getBasicActionsWithComms() {
		return Arrays.asList(Action.NOOP,
				Action.MOVE_UP, Action.MOVE_DOWN, Action.MOVE_LEFT, Action.MOVE_RIGHT,
				Action.FLARE_UP, Action.FLARE_DOWN, Action.FLARE_LEFT, Action.FLARE_RIGHT
				);
	}
	
	
	public static List<Action> generateFlares(int width, int height) {
		List<Action> legalActions = new ArrayList<Action>();
		//generate full grid talk actions
		for (int x = 0; x < width; x++) {
			for (int y=0; y < height; y++) {
				legalActions.add(new AbsoluteFlare(x, y));
			}
		}
		return legalActions;
	}
	
	public static List<Action> generateRelativeFlares() {
		return Arrays.asList(Action.FLARE_DOWN, Action.FLARE_UP, Action.FLARE_LEFT, Action.FLARE_RIGHT);
	}
	
}
