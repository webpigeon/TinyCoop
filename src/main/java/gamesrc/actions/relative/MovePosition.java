package gamesrc.actions.relative;

import api.ActionType;

public class MovePosition extends RelativeAction {
	
	public MovePosition(String name, int dx, int dy) {
		super(name, dx, dy, ActionType.MOVEMENT);
	}
	
}
