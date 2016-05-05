package gamesrc.actions.relative;

import api.ActionType;
import gamesrc.actions.AbstractAction;

public abstract class RelativeAction extends AbstractAction {

	public RelativeAction(String name, int dx, int dy, ActionType type) {
		super(name, dx, dy, type);
	}

	@Override
	public boolean isRelative() {
		return true;
	}

}
