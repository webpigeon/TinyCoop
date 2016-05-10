package api.controller;

import api.Action;
import api.ObservableGameState;

public interface GameObservation extends ObservableGameState {

	public GameObservation simulate(Action ours, Action theirs);
	public void apply(Action ours, Action theirs);
	
	public GameObservation getCopy();
	
}
