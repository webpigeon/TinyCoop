package actions.absolute;

import actions.AbstractAction;
import actions.ActionType;

public class AbsoluteFlare extends AbstractAction {

	public AbsoluteFlare(int x, int y) {
		super("ABS_FLARE("+x+",+"+y+")", x, y, ActionType.FLARE);
	}

	@Override
	public boolean isRelative() {
		return false;
	}

}
