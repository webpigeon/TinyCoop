package gamesrc.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import Controllers.Controller;
import gamesrc.Filters;
import gamesrc.experiment.GameResult;
import gamesrc.experiment.GameRunner;
import gamesrc.level.GameLevel;
import gamesrc.level.LevelParser;

public class GameCli {
	
	public static void main(String[] args) throws ParseException, IOException {
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(buildOptions(), args);
		
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("tinycoop", buildOptions());
			return;
		}
			
		//get number of runs
		String numString = cmd.getOptionValue("runs", "10");
		int numRuns = Integer.parseInt(numString);
		
		//get number of ticks
		String tickString = cmd.getOptionValue("ticks", "2000");
		int tickLimit = Integer.parseInt(tickString);
		
		//generate level list
		String[] levelFiles = cmd.getOptionValues("level");
		if (levelFiles == null) {
			levelFiles = new String[]{
					"data/norm_maps/airlock.txt", 
					"data/norm_maps/butterfly.txt",
					"data/norm_maps/maze.txt",		
					"data/norm_maps/mirror_lock.txt",
					"data/norm_maps/single_door.txt"
			};
		}
		
		String[] firstAgentList = cmd.getOptionValues("p1");
		if (firstAgentList == null) {
			firstAgentList = new String[]{"mcts(500;10;45)", "random"};
		}
		
		String[] secondAgentList = cmd.getOptionValues("p2");
		if (secondAgentList == null) {
			secondAgentList = new String[]{"mcts(500;10;45)", "random", "baisRandom(0.75)", "baisRandom(0.5)", "baisRandom(0.25)"};
		}
		

		Collection<GameRunner> tasks = new ArrayList<GameRunner>();
		for (int i=0; i<numRuns; i++) {
			tasks.addAll(buildRuns(levelFiles, firstAgentList, secondAgentList, tickLimit));
		}

			
		// execute the games
		ExecutorService service = Executors.newFixedThreadPool(4);
		try {
			List<Future<GameResult>> results = service.invokeAll(tasks);
			for (Future<GameResult> r : results) {
				System.out.println(r.get());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		service.shutdown();	
	}
	
	private static Collection<GameRunner> buildRuns(String[] levels, String[] p1, String[] p2, int ticks) throws IOException {
		Collection<GameRunner> tasks = new ArrayList<>();
		
		ControllerUtils utils = new ControllerUtils();
		
		// build games
		for(String levelFile : levels) {
			//build each level
			GameLevel level = LevelParser.buildParser(levelFile);
			level.setLegalMoves("relative", Filters.getAllRelativeActions());

			for (String firstAgent : p1) {
				for (String secondAgent : p2) {
					Controller c1 = utils.parseDescription(firstAgent);
					Controller c2 = utils.parseDescription(secondAgent);
					tasks.add(new GameRunner(level, c1, c2, ticks));
				}
			}
		}
		
		return tasks;
	}
	
	private static Options buildOptions() {
		Options options = new Options();
		
		options.addOption("help", false, "display help message");
		options.addOption("level", false, "Select the level to play");
		options.addOption("p1", false, "select player 1");
		options.addOption("p2", false, "select player 2");
		options.addOption("actions", true, "action set to use");
		
		Option runs = Option.builder("runs").desc("number of repeats (default 1)").build();
		options.addOption(runs);
		
		Option ticks = Option.builder("ticks").desc("maximum number of ticks (default 2000)").build();
		options.addOption(ticks);
		return options;
	}

}
