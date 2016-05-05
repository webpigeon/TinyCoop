package backtrack;

import java.util.Collection;

public interface State {

	public Collection<State> expand();

	public Integer getCost();

}
