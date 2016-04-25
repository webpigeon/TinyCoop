package gamesrc.actions.relative;

import api.ActionType;

public class RelativeFlare extends RelativeAction {

	public RelativeFlare(String name, int dx, int dy) {
		super(name, dx, dy, ActionType.FLARE);
	}

}
