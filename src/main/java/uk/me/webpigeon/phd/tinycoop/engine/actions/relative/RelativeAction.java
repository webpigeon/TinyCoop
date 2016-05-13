package uk.me.webpigeon.phd.tinycoop.engine.actions.relative;

import uk.me.webpigeon.phd.tinycoop.api.ActionType;
import uk.me.webpigeon.phd.tinycoop.engine.actions.AbstractAction;

public abstract class RelativeAction extends AbstractAction {

	public RelativeAction(String name, int dx, int dy, ActionType type) {
		super(name, dx, dy, type);
	}

	@Override
	public boolean isRelative() {
		return true;
	}

}
