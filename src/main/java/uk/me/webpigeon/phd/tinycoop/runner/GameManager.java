package uk.me.webpigeon.phd.tinycoop.runner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import uk.me.webpigeon.phd.tinycoop.api.controller.Controller;
import uk.me.webpigeon.phd.tinycoop.engine.SimpleGame;
import uk.me.webpigeon.phd.tinycoop.engine.level.GameLevel;
import utils.GenerateCSV;

/**
 * Looks after running and logging of games.
 * 
 * This is a multi-threaded runner, therefore the games (and agents) must be threadsafe.
 */
public class GameManager implements Runnable {
	private static final Integer NUM_THREADS = 4;
	private static final Logger LOG = Logger.getLogger(GameManager.class.getCanonicalName());
	
	private final UUID runID;
	private final ExecutorService service;
	private final BlockingQueue<GameResult> results;

	public GameManager() {
		this(NUM_THREADS, UUID.randomUUID());
	}
	
	public GameManager(UUID runID) {
		this(NUM_THREADS, runID);
	}

	public GameManager(int threads, UUID runID) {
		this.runID = runID;
		this.service = Executors.newFixedThreadPool(threads);
		this.results = new ArrayBlockingQueue<GameResult>(10);
		LOG.info("runner "+runID+" started with "+threads);
	}

	public void addGame(GameLevel level, Controller p1, Controller p2) {
		SimpleGame game = new SimpleGame(level);
		GameSetup setup = new GameSetup(p1.getFriendlyName(), p2.getFriendlyName(), level.getLevelName(), level.getActionSetName());
		GameExecutor ec = new GameExecutor(setup, game, p1, p2);
		GameWrapper wrapper = new GameWrapper(results, ec);
		service.submit(wrapper);
		LOG.info("game "+setup+" added to "+runID);
	}

	@Override
	public void run() {
		LOG.info("runner collection started for "+runID);
		
		//this is used to keep track of restarts
		try (
				OutputStream out = new FileOutputStream(String.format("results-%s.csv", runID), true);
				){
			GenerateCSV csv = new GenerateCSV(out);
			csv.writeLine("gameID","levelID","actionSet","player1","player2","result","score","ticks","wallTime","userTime","runID");
			
			try {
				while (!Thread.interrupted()) {
					GameResult result = results.poll(5, TimeUnit.MINUTES);
					
					//detect timeouts and respond correctly
					if (result != null) {
						LOG.info("game result collected for "+runID+" "+result);
						csv.writeLine(result.setup.id,
								result.setup.level,
								result.setup.actionSet,
								result.setup.p1,
								result.setup.p2,
								result.result,
								result.score,
								result.ticks,
								result.wallTime,
								result.cpuTime,
								runID);
					}
	
					//only shut down if there are no more jobs to process
					//you can interrupt the thread, but might loose data if you do.
					if (service.isTerminated() && results.isEmpty()) {
						LOG.info("service terminated and no more data for "+runID);
						break;
					}
					
				}
			} catch (InterruptedException ex) {
				LOG.severe("Interrupted: "+runID+" "+ex);
				ex.printStackTrace();
			}
			
			csv.close();
			
		} catch (IOException ex) {
			LOG.severe("Exception: "+runID+" "+ex);
			ex.printStackTrace();
			return;
		}

	}
	
	/**
	 * Gracefully shutdown the manager.
	 */
	public void shutdown() {
		service.shutdown();
		LOG.fine("shutdown of service for "+runID+" completed");
		try {
			service.awaitTermination(365, TimeUnit.DAYS);
		} catch (InterruptedException ex) {
			LOG.severe("WTF, it took you a YEAR to shutdown your service?");
		}
		LOG.info("shutdown termination complete for:"+runID);
	}

}
