package runner.tinycoop;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 * Wraps a game run and report the completion.
 * 
 * This may seem a little silly but it lets the game logic and multi-threading logic be
 * completely indipendent, this is important to still allow single threaded games.
 */
public class GameWrapper implements Callable<GameResult> {
	private final BlockingQueue<GameResult> queue;
	private final GameExecutor executor;
	
	public GameWrapper(BlockingQueue<GameResult> queue, GameExecutor executor){
		this.queue = queue;
		this.executor = executor;
	}

	@Override
	public GameResult call() throws Exception {
		GameResult result = executor.call();
		queue.put(result);
		return result;
	}

}
