class Game {
	int[] coasters;

	public Game(int... numbers) {
		coasters = numbers;
	}

	public int getWins() {

		int wins = 0;
		// 7 possible choices
		for (int i = 0; i < coasters.length; i++) {
			int nextPoint = i;
			boolean[] turnedOver = new boolean[coasters.length];
			turnedOver[i] = true;
			int turns = 1;
			while (true) {
				nextPoint = coasters[nextPoint];
				turns++;
				if (turnedOver[nextPoint]) { // lost here
					break;
				} else {
					turnedOver[nextPoint] = true;
				}
			}
			if (turns == coasters.length)
				wins++;
		}
		return wins;
	}
}

/**
 * Created by pwillic on 29/06/2015.
 */
public class LuckySevens {

	public static void main(String[] args) {
		int totalGamesPlayed = 0;
		int totalGameStates = 0;
		int totalWins = 0;
		int noChoice = 0;
		boolean first = true;

		for (int a = 0; a < 7; a++) {
			for (int b = 0; b < 7; b++) {
				for (int c = 0; c < 7; c++) {
					for (int d = 0; d < 7; d++) {
						for (int e = 0; e < 7; e++) {
							for (int f = 0; f < 7; f++) {
								for (int g = 0; g < 7; g++) {
									Game game = new Game(a, b, c, d, e, f, g);
									int wins = game.getWins();
									if (wins == 0 || wins == 7) {
										// System.out.println(wins);
										noChoice++;
									} else {
										if (first) {
											System.out.println(
													a + "," + b + "," + c + "," + d + "," + e + "," + f + "," + g);
											first = false;
										}
									}
									totalWins += wins;
									totalGamesPlayed += 7;
									totalGameStates++;
								}
							}
						}
					}
				}
			}
		}

		System.out.println("Total Game States: " + totalGameStates);
		System.out.println("Total Games: " + totalGamesPlayed);
		System.out.println("Total Wins: " + totalWins);
		System.out.println("Total Times Your Choice Didn't Matter: " + noChoice);
		System.out.println("Total Times your choice mattered: " + (totalGameStates - noChoice));
	}
}
