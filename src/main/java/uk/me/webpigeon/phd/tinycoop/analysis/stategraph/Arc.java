package uk.me.webpigeon.phd.tinycoop.analysis.stategraph;

import uk.me.webpigeon.phd.tinycoop.api.Action;

public class Arc {
	StateAbstraction from;
	StateAbstraction to;
	Action action1;
	Action action2;
	int cost;
}
