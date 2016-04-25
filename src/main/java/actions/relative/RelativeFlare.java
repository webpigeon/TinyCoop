package actions.relative;

import actions.ActionType;

public class RelativeFlare extends RelativeAction {

	public RelativeFlare(String name, int dx, int dy) {
		super(name, dx, dy, ActionType.FLARE);
	}

}
