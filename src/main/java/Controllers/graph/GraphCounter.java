package Controllers.graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import api.Action;
import api.GameState;
import api.ObservableGameState;
import gamesrc.Filters;
import gamesrc.SimpleGame;
import gamesrc.level.GameLevel;
import gamesrc.level.LevelParser;

public class GraphCounter {

	public static void main(String[] args) throws IOException {
		
		String[] levels = new String[]{
			//normalised maps
			//"data/norm_maps/airlock.txt",
			"data/norm_maps/butterfly.txt",
			/*"data/norm_maps/empty.txt",
			"data/norm_maps/maze.txt",
			"data/norm_maps/mirror_lock.txt",
			"data/norm_maps/single_door.txt",
			
			//non-normalised maps
			"data/maps/airlock.txt",
			"data/maps/butterflies.txt",
			"data/maps/duellock.txt",
			"data/maps/extended_side.txt",
			"data/maps/pathfinding.txt",
			"data/maps/side_by_side.txt",
			"data/maps/symmetric_single_door.txt"*/
		};
		

		for (String level : levels) {
			long startTime = System.currentTimeMillis();
			
			GameLevel levelRel = LevelParser.buildParser(level);
			levelRel.setLegalMoves("relative", Filters.getBasicActions());
			ObservableGameState start = new SimpleGame(levelRel);
			
			StateGraph graph = new StateGraph();
			
			System.out.println("computing graph for "+level);
			int goalStates = 0;
			LinkedList<ObservableGameState> expanders = new LinkedList<>();
			expanders.add(start);
			
			while (!expanders.isEmpty()) {
				ObservableGameState parent = expanders.poll();
				StateAbstraction parentAbs = makeAbstraction(parent);
	
				int newStates = 0;
				int revisits = 0;
				
				for (Action p1Action : parent.getLegalActions(0)) {
					for (Action p2Action : parent.getLegalActions(1)) {
						
						ObservableGameState state = (ObservableGameState)parent.getClone();
						state.update(p1Action, p2Action);
						StateAbstraction stateAbs = makeAbstraction(state);
						
						if (!graph.contains(stateAbs)) {
							if (!state.hasWon()) {
								expanders.push(state);
							} else {
								//don't add the state to the expansion list if we won already.
								goalStates++;
							}
							graph.addVertex(stateAbs);
							newStates++;
						} else {
							revisits++;
						}
						
						graph.addTransision(parentAbs, stateAbs, p1Action, p2Action);
					}
				}
				System.out.println(parent + " new states: "+newStates+", revisits: "+revisits+" total left to expand: "+expanders.size());
			}
			long endTime = System.currentTimeMillis();
			long delta = endTime - startTime;
			
			System.out.println(":: level: "+level+" (graph took "+delta+"ms to complete) ");
			System.out.println(":: there are "+goalStates+" goal states");
			System.out.println(":: total states: "+graph.getVertexCount());
			System.out.println(":: total arcs: "+graph.getArcCount()+" ("+graph.getArcNoopCount()+" noops)");
		}
	}
	
	public static StateAbstraction makeAbstraction(ObservableGameState state) {
		return new FullState(state);
	}

}
