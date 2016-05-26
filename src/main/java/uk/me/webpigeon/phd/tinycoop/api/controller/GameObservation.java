package uk.me.webpigeon.phd.tinycoop.api.controller;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.ObservableGameState;

public interface GameObservation extends ObservableGameState {

	public void apply(Action ours, Action theirs);

	public GameObservation getCopy();

	public GameObservation simulate(Action ours, Action theirs);
	
	public GameObservation fromPerspective(int newAgent);

	public void setNewSeed(int seed);

}
