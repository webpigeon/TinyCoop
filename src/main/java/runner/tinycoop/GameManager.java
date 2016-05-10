package runner.tinycoop;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import api.controller.Controller;
import gamesrc.SimpleGame;
import gamesrc.level.GameLevel;

/**
 * Looks after running and logging of games.
 */
public class GameManager implements Runnable {
	private static final Integer NUM_THREADS = 4;
	private ExecutorService service;
	private BlockingQueue<GameResult> results;
	
	public GameManager(){
		this(NUM_THREADS);
	}
	
	public GameManager(int threads){
		this.service = Executors.newFixedThreadPool(threads);
		this.results = new ArrayBlockingQueue<GameResult>(10);
	}
	
	public void addGame(GameLevel level, Controller p1, Controller p2) {
		SimpleGame game = new SimpleGame(level);
		GameExecutor ec = new GameExecutor(game, p1, p2);
		GameWrapper wrapper = new GameWrapper(results, ec);
		service.submit(wrapper);
	}

	@Override
	public void run() {
		
		try {
			while(!Thread.interrupted()) {
				GameResult result = results.take();
				System.out.println(result);
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		
	}
	

}
