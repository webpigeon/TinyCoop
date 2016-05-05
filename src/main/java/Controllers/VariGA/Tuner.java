package Controllers.VariGA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import Controllers.Controller;
import FastGame.CoopGame;

class Candidate {
	public static Candidate values;
	private static Random random = new Random();

	public static Candidate crossover(Candidate p1, Candidate p2) {
		Candidate child = new Candidate(false);

		for (int i = 0; i < p1.chances.length; i++) {
			child.chances[i] = random.nextBoolean() ? p1.chances[i] : p2.chances[i];
		}

		boolean firstParams = random.nextBoolean();
		boolean secondParams = random.nextBoolean();

		child.params[0] = (firstParams) ? p1.params[0] : p2.params[0];
		child.params[1] = (firstParams) ? p1.params[1] : p2.params[1];
		child.params[2] = (secondParams) ? p1.params[2] : p2.params[2];
		child.params[3] = (secondParams) ? p1.params[3] : p2.params[3];

		return child;
	}

	public static Candidate mutate(Candidate p1) {
		Candidate child = p1.getClone();
		for (int i = 0; i < child.chances.length; i++) {
			if (random.nextDouble() < 0.25) {
				child.chances[i] += (random.nextDouble()) / 5 - 0.1;
				if (child.chances[i] > 1.0)
					child.chances[i] = 1.0;
				if (child.chances[i] < 0.0)
					child.chances[i] = 0.0;
			}
		}

		if (random.nextDouble() < 0.25) {
			child.params[0] += random.nextBoolean() ? 1 : -1;
			if (child.params[0] == child.params[1])
				child.params[1]++;
		}

		if (random.nextDouble() < 0.25) {
			child.params[1] += random.nextBoolean() ? 1 : -1;
			if (child.params[1] == child.params[0])
				child.params[1]++;
		}

		if (random.nextDouble() < 0.25) {
			child.params[2] += random.nextBoolean() ? 1 : -1;
			if (child.params[2] == child.params[3])
				child.params[3]++;
		}

		if (random.nextDouble() < 0.25) {
			child.params[3] += random.nextBoolean() ? 1 : -1;
			if (child.params[3] == child.params[2])
				child.params[3]++;
		}
		return child;
	}

	double[] chances;

	int[] params;

	private Double fitness = null;

	public Candidate(boolean randomise) {
		chances = new double[3];
		params = new int[4];
		if (randomise)
			randomise();
	}

	public Candidate getClone() {
		Candidate other = new Candidate(false);
		System.arraycopy(this.chances, 0, other.chances, 0, this.chances.length);
		System.arraycopy(this.params, 0, other.params, 0, this.params.length);
		other.fitness = this.fitness;
		return other;
	}

	private void randomise() {
		// for (int i = 0; i < chances.length; i++) {
		// chances[i] = random.nextDouble();
		// }
		chances[0] = 0.25;
		chances[1] = 0.8;
		chances[2] = 0.75;

		boolean finished = false;
		while (!finished) {
			params[0] = random.nextInt(15) + 1;
			params[1] = random.nextInt(15) + 1;
			finished = params[0] < params[1];

		}

		finished = false;
		while (!finished) {
			params[2] = random.nextInt(5) + 1;
			params[3] = random.nextInt(5) + 1;
			finished = params[2] < params[3];

		}
	}

	public void refine() {
		chances = new double[] { 0.25, 0.8, 0.75 };
		boolean finished = false;
		while (!finished) {
			params[0] = random.nextInt(15) + 1;
			params[1] = random.nextInt(15) + 1;
			finished = params[0] < params[1];

		}

		finished = false;
		while (!finished) {
			params[2] = random.nextInt(8) + 1;
			params[3] = random.nextInt(8) + 1;
			finished = params[2] < params[3];

		}
	}
}

/**
 * See if we can tune VariGA parameters here Created by Piers on 10/07/2015.
 */
public class Tuner {

	// Same tactic as the sampler in the other thing

	public static void main(String[] args) throws UnknownHostException {
		Controller c1;
		Controller c2;

		int threadNumber = Integer.parseInt(args[0]);

		String[] levels = new String[] { "level1.txt", "level1E.txt", "level3.txt", "level4.txt", "level5.txt",
				"level6.txt" };

		ArrayList<String> header = new ArrayList<>();
		header.add("numChance");
		header.add("lengthChance");
		header.add("actionChance");
		header.add("minNum");
		header.add("maxNum");
		header.add("minLength");
		header.add("maxLength");

		for (String level : levels) {
			header.add(level + "-Score");
			header.add(level + "-TickS");
		}
		String fileName = "Data/" + InetAddress.getLocalHost().getHostName() + "-thread" + threadNumber + ".csv";
		if (!(new File(fileName).exists())) {
			write(fileName, header.toArray());
		}

		int candidates = 0;
		// noinspection InfiniteLoopStatement
		while (true) {
			System.out.println("Starting: " + candidates);
			Candidate candidate = new Candidate(true);
			c1 = new VariGA(true, 500, candidate.chances, candidate.params);
			c2 = new VariGA(false, 500, candidate.chances, candidate.params);
			double[][] results = runGames(c1, c2, levels, candidates);
			ArrayList<Object> list = new ArrayList<>();
			list.add(candidate.chances[0]);
			list.add(candidate.chances[1]);
			list.add(candidate.chances[2]);
			list.add(candidate.params[0]);
			list.add(candidate.params[1]);
			list.add(candidate.params[2]);
			list.add(candidate.params[3]);

			for (int i = 0; i < results.length; i++) {
				list.add(results[i][0]);
				list.add(results[i][1]);
			}

			write(fileName, list.toArray());

			System.out.println("Finished: " + candidates);
			candidates++;
		}
	}

	public static double[][] runGames(Controller c1, Controller c2, String[] games, int candidates) {
		String levelPath = "data/maps/";
		int gamesPerMatchup = 5;

		double[][] results = new double[games.length][2];

		for (int j = 0; j < gamesPerMatchup; j++) {
			for (int i = 0; i < games.length; i++) {
				String level = games[i];
				CoopGame game = new CoopGame(levelPath + level);
				System.out.println("Candidate-" + candidates + " Starting: " + level + "-" + j);
				int ticksTaken = 0;
				while (ticksTaken < 2000 && !game.hasWon()) {
					if (ticksTaken % 100 == 0)
						System.out.println("Tick: " + ticksTaken);
					game.update(c1.get(game.getClone()), c2.get(game.getClone()));
					ticksTaken++;
				}
				results[i][0] += game.getScore();
				results[i][1] += ticksTaken;
			}
		}
		return results;
	}

	public static void write(String fileName, Object... arguments) {
		try {

			File file = new File(fileName);
			FileWriter writer = new FileWriter(fileName, file.exists());

			StringBuilder line = new StringBuilder();
			for (Object piece : arguments) {
				line.append(piece);
				line.append(",");
			}

			line.deleteCharAt(line.lastIndexOf(",")); // clear last ","

			writer.write(line.toString());
			writer.write("\n");
			writer.flush();

			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}