package gamesrc.actions.absolute;

import api.ActionType;
import gamesrc.actions.AbstractAction;

public class AbsoluteFlare extends AbstractAction {

	public AbsoluteFlare(int x, int y) {
		super("ABS_FLARE(" + x + ",+" + y + ")", x, y, ActionType.FLARE);
	}

	@Override
	public boolean isRelative() {
		return false;
	}

}
