package gamesrc;

import java.util.ArrayList;
import java.util.List;

import FastGame.Action;

public class Filters {

	/**
	 * Filter all actions and leave only movement actions
	 * 
	 * @param sourceActions
	 * @return
	 */
	public static List<Action> filterMovement(Action[] sourceActions){
		List<Action> l = new ArrayList<Action>();
		for (Action action : sourceActions) {
			if(action.isMovement()) {
				l.add(action);
			}
		}
		return l;
	}
	
	/**
	 * Filter all actions and leave only communication actions
	 * 
	 * @param sourceActions
	 * @return
	 */
	public static List<Action> filterTalk(Action[] sourceActions){
		List<Action> l = new ArrayList<Action>();
		for (Action action : sourceActions) {
			if(action.isTalk()) {
				l.add(action);
			}
		}
		return l;
	}
	
}
