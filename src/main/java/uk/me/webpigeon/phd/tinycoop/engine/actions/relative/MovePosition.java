package uk.me.webpigeon.phd.tinycoop.engine.actions.relative;

import uk.me.webpigeon.phd.tinycoop.api.ActionType;

public class MovePosition extends RelativeAction {

	public MovePosition(String name, int dx, int dy) {
		super(name, dx, dy, ActionType.MOVEMENT);
	}

}
