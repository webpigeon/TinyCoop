package uk.me.webpigeon.phd.tinycoop.engine.actions.absolute;

import uk.me.webpigeon.phd.tinycoop.api.ActionType;
import uk.me.webpigeon.phd.tinycoop.engine.actions.AbstractAction;

public class AbsoluteFlare extends AbstractAction {

	public AbsoluteFlare(int x, int y) {
		super("ABS_FLARE(" + x + ",+" + y + ")", x, y, ActionType.FLARE);
	}

	@Override
	public boolean isRelative() {
		return false;
	}

}
