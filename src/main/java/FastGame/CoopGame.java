package FastGame;

import static FastGame.GroundTypes.WALKABLE;
import static FastGame.ObjectTypes.AGENT;
import static FastGame.ObjectTypes.BUTTON;
import static FastGame.ObjectTypes.DOOR;
import static FastGame.ObjectTypes.GOAL;
import static FastGame.ObjectTypes.NO_OBJECT;
import static FastGame.ObjectTypes.NUMBER_OF_LAYERS;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.GameState;
import uk.me.webpigeon.phd.tinycoop.engine.Filters;

/**
 * Created by pwillic on 25/06/2015.
 */
public class CoopGame implements GameState {

	public static int getIDFromValue(int value) {
		return value - (getItemTypeFromValue(value) * 1000);
	}

	public static int getItemTypeFromValue(int value) {
		return (int) (Math.floor(value / 1000.0d));
	}

	public static int getValueFromIdAndType(int type, int id) {
		return (type * 1000) + id;
	}

	private int[] data;

	private int width, height, layers;
	// need to check that all agents reach all goals
	private boolean[] goalSet = new boolean[1];
	private int maxID;
	private boolean[] encountered;
	private boolean[] doorOpen;

	private double score = 0;

	private int[] maxIDs;

	private int[] agentLocations;

	// volatile, required for A*
	private Map<Integer, Point> playerPos = new TreeMap<>();

	public CoopGame(int width, int height) {
		initialiseData(width, height, NUMBER_OF_LAYERS);
	}

	/**
	 * Opens a file and generates the game from that
	 *
	 * @param fileName
	 */
	public CoopGame(String fileName) {
		initialiseData(fileName);
	}

	private void calculateScore() {
		score = 0.0d;
		for (Boolean beenThere : goalSet) {
			if (beenThere)
				score += 1.0 / goalSet.length;
		}
		// System.out.println("GoalSet Length" + goalSet + " : " +
		// Arrays.toString(goalSet));
		// System.out.println("Score is: " + score);
	}

	public boolean doorOpen(int doorID) {
		return doorOpen[doorID];
	}

	private void encounter(int objectType, int objectID) {
		encountered[objectID + (objectType * maxID)] = true;
	}

	private void findAgents() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int value = get(x, y, AGENT);
				if (value != 0) {
					int agentID = getIDFromValue(value);
					setAgentLocation(agentID, x, y);
				}
			}
		}
	}

	public int get(int x, int y, int layer) {
		return data[layer + layers * (y + height * (x))];
		// return data[x + height * (y + width * layer)];
	}

	@Override
	public int getActionLength() {
		return 5;
	}

	private int getAgentX(int agentID) {
		return agentLocations[(agentID * 2)];
	}

	private int getAgentY(int agentID) {
		return agentLocations[1 + (agentID * 2)];
	}

	@Override
	public CoopGame getClone() {
		CoopGame other = new CoopGame(this.width, this.height);
		System.arraycopy(this.data, 0, other.data, 0, this.data.length);
		other.goalSet = new boolean[this.goalSet.length];
		System.arraycopy(this.goalSet, 0, other.goalSet, 0, this.goalSet.length);
		other.doorOpen = new boolean[this.maxID];
		System.arraycopy(this.doorOpen, 0, other.doorOpen, 0, this.doorOpen.length);
		other.maxID = this.maxID;
		other.maxIDs = new int[this.maxIDs.length];
		System.arraycopy(this.maxIDs, 0, other.maxIDs, 0, this.maxIDs.length);
		other.score = this.score;
		other.playerPos = new HashMap<>(this.playerPos);
		other.agentLocations = new int[this.agentLocations.length];
		System.arraycopy(this.agentLocations, 0, other.agentLocations, 0, this.agentLocations.length);
		return other;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public int getLayers() {
		return layers;
	}

	@Override
	public List<Action> getLegalActions(int playerID) {
		return Filters.getBasicActions();
	}

	public Point getPos(int id) {
		return playerPos.get(id);
	}

	@Override
	public double getScore() {
		return score;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public boolean hasWon() {
		if (goalSet == null)
			return true;
		for (boolean goal : goalSet) {
			if (!goal)
				return false;
		}
		return true;
	}

	private boolean haveEncountered(int objectType, int objectID) {
		return encountered[objectID + (objectType * maxID)];
	}

	private void initialiseData(int width, int height, int layers) {
		data = new int[width * height * layers];
		this.width = width;
		this.height = height;
		this.layers = layers;
	}

	private void initialiseData(String fileName) {
		int width = 0;
		int height = 0;
		int maxID = 0;
		int[] maxIDs = new int[NUMBER_OF_LAYERS];

		try {
			Scanner scanner = new Scanner(new FileInputStream(fileName));
			int lineNumber = 0;
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] lineParts = line.split(" ");
				if (lineNumber == 0) {
					width = Integer.parseInt(line);
				} else if (lineNumber == 1) {
					height = Integer.parseInt(line);
					initialiseData(width, height, NUMBER_OF_LAYERS);
				} else if (lineNumber >= 2 && lineNumber < 2 + height) {
					// Read a line
					int y = lineNumber - 2;
					for (int x = 0; x < lineParts.length; x++) {
						set(x, y, 0, Integer.parseInt(lineParts[x]));
					}
				} else {
					if (lineParts.length >= 2 && !lineParts[1].equals("OBJECTS")) {
						// Make an object
						int objectType = Integer.parseInt(lineParts[0]);
						int objectID = Integer.parseInt(lineParts[3]);
						int value = (objectType * 1000) + objectID;
						if (objectID > maxID) {
							maxID = objectID;
						}
						if (objectID > maxIDs[objectType])
							maxIDs[objectType] = objectID;
						int x = Integer.parseInt(lineParts[1]);
						int y = Integer.parseInt(lineParts[2]);
						set(x, y, objectType, value);
						if (objectType == AGENT) {
							playerPos.put(objectID, new Point(x, y));
						}
					}
				}
				lineNumber++;
			}
			goalSet = new boolean[(maxIDs[GOAL] + 1) * (maxIDs[AGENT] + 1)];
			// System.out.println(maxIDs[DOOR] + 1);
			doorOpen = new boolean[maxIDs[DOOR] + 1];
			agentLocations = new int[(maxIDs[AGENT] + 1) * 2];
			this.maxID = maxID + 1;
			this.maxIDs = maxIDs;
			findAgents();
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void reachGoal(int goalID, int agentID) {
		if (!goalSet[agentID + (goalID * (maxIDs[AGENT] + 1))]) {
			goalSet[agentID + (goalID * (maxIDs[AGENT] + 1))] = true;
			calculateScore();
		}
	}

	private void resetEncountered() {
		encountered = new boolean[NUMBER_OF_LAYERS * maxID];
	}

	// Only agents can collide with stuff - speed things up
	private void runCollisionDetection() {
		doorOpen = new boolean[doorOpen.length];

		for (int agentID = 0; agentID <= maxIDs[AGENT]; agentID++) {
			int x = getAgentX(agentID);
			int y = getAgentY(agentID);
			for (int layer = 1; layer < NUMBER_OF_LAYERS; layer++) {
				if (layer == AGENT)
					continue;
				// check collision
				int value = get(x, y, layer);
				if (value == 0)
					continue;
				int type = getItemTypeFromValue(value);
				// System.out.println(type);
				if (type == BUTTON) {
					int buttonID = getIDFromValue(value);
					// System.out.println("Collided with button");
					doorOpen[buttonID] = true;
				} else if (type == GOAL) {
					int goalID = getIDFromValue(value);
					reachGoal(goalID, agentID);
				}
			}
		}
	}

	private void runUpdateLoop(Action... actions) {
		for (int agentID = 0; agentID < actions.length; agentID++) {
			Action action = actions[agentID];
			if (action.equals(FastAction.NOOP))
				continue;
			int x = getAgentX(agentID);
			int y = getAgentY(agentID);

			int newX = x + action.getX();
			int newY = y + action.getY();

			if (WALKABLE[get(newX, newY, 0)]) {
				// is a door closed there?
				if (get(newX, newY, DOOR) != 0 && !doorOpen(getIDFromValue(get(newX, newY, DOOR)))) {
					continue;
				}
				if (get(newX, newY, AGENT) != 0) {
					// other agent is here
					continue;
				}
				set(x, y, AGENT, NO_OBJECT);
				set(newX, newY, AGENT, getValueFromIdAndType(AGENT, agentID));
				setAgentLocation(agentID, newX, newY);
				encounter(AGENT, agentID);
				playerPos.put(agentID, new Point(newX, newY));
			}
		}
	}

	private void runUpdateLoop(FastAction first, FastAction second) {

		if (first.equals(FastAction.NOOP) && second.equals(FastAction.NOOP))
			return;
		// System.out.println("First: " + first + " Second: " + second);
		resetEncountered();
		for (int agentID = 0; agentID <= maxIDs[AGENT]; agentID++) {
			int x = getAgentX(agentID);
			int y = getAgentY(agentID);
			int newX = x + ((agentID == 0) ? first : second).getX();
			int newY = y + ((agentID == 0) ? first : second).getY();
			// System.out.println("X:" + x + "Y:" + y + "nX" + newX + "nY" +
			// newY);

			// Can we go there?
			if (WALKABLE[get(newX, newY, 0)]) {
				// is a door closed there?
				if (get(newX, newY, DOOR) != 0 && !doorOpen(getIDFromValue(get(newX, newY, DOOR)))) {
					continue;
				}
				if (get(newX, newY, AGENT) != 0) {
					// other agent is here
					continue;
				}
				set(x, y, AGENT, NO_OBJECT);
				set(newX, newY, AGENT, getValueFromIdAndType(AGENT, agentID));
				setAgentLocation(agentID, newX, newY);
				encounter(AGENT, agentID);
				playerPos.put(agentID, new Point(newX, newY));
			}
		}
	}

	private void set(int x, int y, int layer, int value) {
		data[layer + layers * (y + height * (x))] = value;
	}

	private void setAgentLocation(int agentID, int x, int y) {
		agentLocations[agentID * 2] = x;
		agentLocations[1 + (agentID * 2)] = y;
	}

	// Forward model update loop
	@Override
	public void update(Action first, Action second) {
		runCollisionDetection();
		runUpdateLoop(first, second);
	}

	public void update(FastAction... actions) {
		runCollisionDetection();
		runUpdateLoop(actions);
	}

}
