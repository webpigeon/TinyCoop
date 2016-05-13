package uk.me.webpigeon.phd.tinycoop.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.ActionType;
import uk.me.webpigeon.phd.tinycoop.engine.actions.absolute.AbsoluteFlare;
import uk.me.webpigeon.phd.tinycoop.engine.actions.relative.MovePosition;
import uk.me.webpigeon.phd.tinycoop.engine.actions.relative.RelativeFlare;

public class Filters {
	public static final Action MOVE_UP = new MovePosition("MOVE_UP", 0, -1);
	public static final Action MOVE_DOWN = new MovePosition("MOVE_DOWN", 0, 1);
	public static final Action MOVE_LEFT = new MovePosition("MOVE_LEFT", -1, 0);
	public static final Action MOVE_RIGHT = new MovePosition("MOVE_RIGHT", 1, 0);
	public static final Action FLARE_UP = new RelativeFlare("FLARE_UP", 0, -1);
	public static final Action FLARE_DOWN = new RelativeFlare("FLARE_DOWN", 0, 1);
	public static final Action FLARE_LEFT = new RelativeFlare("FLARE_LEFT", -1, 0);
	public static final Action FLARE_RIGHT = new RelativeFlare("FLARE_RIGHT", 1, 0);

	/**
	 * Filter all actions and leave only movement actions
	 *
	 * @param myActions
	 * @return
	 */
	public static List<Action> filterMovement(List<Action> myActions) {
		return filterSet(myActions, ActionType.MOVEMENT);
	}

	public static List<Action> filterSet(List<Action> src, ActionType... types) {
		List<Action> filtered = new ArrayList<Action>();

		for (Action action : src) {
			if (matches(action.getType(), types)) {
				filtered.add(action);
			}
		}

		return filtered;
	}

	/**
	 * Filter all actions and leave only communication actions
	 *
	 * @param sourceActions
	 * @return
	 */
	public static List<Action> filterTalk(List<Action> sourceActions) {
		return filterSet(sourceActions, ActionType.FLARE);
	}

	public static List<Action> generateFlares(int width, int height) {
		List<Action> legalActions = new ArrayList<Action>();
		// generate full grid talk actions
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				legalActions.add(new AbsoluteFlare(x, y));
			}
		}
		return legalActions;
	}

	public static List<Action> generateRelativeFlares() {
		return Arrays.asList(FLARE_DOWN, FLARE_UP, FLARE_LEFT, FLARE_RIGHT);
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
		return Arrays.asList(Action.NOOP, MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT);
	}

	public static List<Action> getBasicActionsWithComms() {
		return Arrays.asList(Action.NOOP, MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT, FLARE_UP, FLARE_DOWN, FLARE_LEFT,
				FLARE_RIGHT);
	}

	private static boolean matches(ActionType type, ActionType... actionTypes) {
		if (type == null)
			return false;
		for (ActionType other : actionTypes) {
			if (type.equals(other)) {
				return true;
			}
		}
		return false;
	}

}
