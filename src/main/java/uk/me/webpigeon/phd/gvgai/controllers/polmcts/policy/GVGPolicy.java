package uk.me.webpigeon.phd.gvgai.controllers.polmcts.policy;

import uk.me.webpigeon.phd.gvgai.StateObservationMulti;
import uk.me.webpigeon.phd.tinycoop.api.Action;

public interface GVGPolicy {
	
	public Action getActionAt(Action myAction, StateObservationMulti multi);

}
