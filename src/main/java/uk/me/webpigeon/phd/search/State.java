package uk.me.webpigeon.phd.search;

import java.util.Collection;

public interface State {

	public Collection<State> expand();

	public Integer getCost();

}
