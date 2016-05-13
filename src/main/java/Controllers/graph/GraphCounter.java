package Controllers.graph;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import api.Action;
import api.GameObject;
import api.ObservableGameState;
import gamesrc.Filters;
import gamesrc.SimpleGame;
import gamesrc.level.GameLevel;
import gamesrc.level.LevelParser;

public class GraphCounter {

	private static int getFloor(ObservableGameState state, int type) {
		int floors = 0;
		for (int x = 0; x < state.getWidth(); x++) {
			for (int y = 0; y < state.getHeight(); y++) {
				if (state.getFloor(x, y) == type) {
					floors++;
				}
			}
		}
		return floors;
	}

	private static Map<String, Integer> getObjectType(ObservableGameState state) {
		Map<String, Integer> histogram = new HashMap<String, Integer>();

		for (int x = 0; x < state.getWidth(); x++) {
			for (int y = 0; y < state.getHeight(); y++) {
				GameObject o = state.getObject(x, y);
				if (o == null) {
					continue;
				}
				String key = o.getType() + "(" + o.getSignal() + ")";
				Integer count = histogram.get(key);
				if (count == null) {
					count = 0;
				}
				count++;
				histogram.put(key, count);
			}
		}
		return histogram;
	}

	public static void main(String[] args) throws IOException {

		PrintStream ps = new PrintStream("levelStats.txt");

		String[] levelStr = new String[] {
				// normalised maps
				/*"data/norm_maps/airlock.txt",
				// "data/norm_maps/butterfly.txt",
				"data/norm_maps/empty.txt", "data/norm_maps/maze.txt", "data/norm_maps/mirror_lock.txt",
				"data/norm_maps/single_door.txt",*/

				// non-normalised maps
				/*"data/maps/airlock.txt", "data/maps/butterflies.txt", "data/maps/duellock.txt",
				"data/maps/extended_side.txt", "data/maps/pathfinding.txt", "data/maps/side_by_side.txt",
				"data/maps/symmetric_single_door.txt"*/
				"data/norm_maps/cloverleaf.txt"
		};

		for (String level : levelStr) {

			GameLevel simpleLevel = LevelParser.buildParser(level);
			simpleLevel.setLegalMoves("basic", Filters.getBasicActions());

			GameLevel[] levels = new GameLevel[] { simpleLevel };

			for (GameLevel levelRel : levels) {
				long startTime = System.currentTimeMillis();

				ObservableGameState start = new SimpleGame(levelRel);

				StateGraph graph = new StateGraph();

				System.out.println("computing graph for " + level);
				int goalStates = 0;
				LinkedList<ObservableGameState> expanders = new LinkedList<>();
				expanders.add(start);

				while (!expanders.isEmpty()) {
					ObservableGameState parent = expanders.poll();
					StateAbstraction parentAbs = makeAbstraction(parent);

					for (Action p1Action : parent.getLegalActions(0)) {
						for (Action p2Action : parent.getLegalActions(1)) {

							ObservableGameState state = (ObservableGameState) parent.getClone();
							state.update(p1Action, p2Action);
							StateAbstraction stateAbs = makeAbstraction(state);

							if (!graph.contains(stateAbs)) {
								if (!state.hasWon()) {
									expanders.push(state);
								} else {
									// don't add the state to the expansion list
									// if we won already.
									goalStates++;
								}
								graph.addVertex(stateAbs);
							}

							graph.addTransision(parentAbs, stateAbs, p1Action, p2Action);
						}
					}
					// System.out.println(parent + " new states: "+newStates+",
					// revisits: "+revisits+" total left to expand:
					// "+expanders.size());
				}
				long endTime = System.currentTimeMillis();
				long delta = endTime - startTime;

				ps.println("Level " + level + " actionSet: " + levelRel.getActionSetName());
				ps.println(":: level: " + level + " (graph took " + delta + "ms to complete) ");
				ps.println(":: floors: " + getFloor(start, 0) + ", walls: " + getFloor(start, 1));
				ps.println(":: goals: " + start.getGoalsCount());
				ps.println(":: objects: " + getObjectType(start));
				ps.println(":: there are " + goalStates + " goal states");
				ps.println(":: total states: " + graph.getVertexCount());
				ps.println(":: total arcs: " + graph.getArcCount() + " (" + graph.getArcNoopCount() + " noops)");
				ps.println();
			}
		}

		ps.close();
	}

	public static StateAbstraction makeAbstraction(ObservableGameState state) {
		return new PosAndVisitList(state);
	}

}
