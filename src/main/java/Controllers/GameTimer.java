package Controllers;

/**
 * Created by pwillic on 14/05/2015.
 */
public class GameTimer {

	long startTime;
	long timeBudget;

	public GameTimer() {
		startTime = System.nanoTime();
	}

	public long elapsed() {
		return System.nanoTime() - startTime;
	}

	public long elapsedMilliseconds() {
		return (long) (elapsed() / 1.0E6);
	}

	public boolean exceeded() {
		return elapsed() > timeBudget;
	}

	private long remainingTime() {
		return (timeBudget - elapsed());
	}

	public long remainingTimeMilliseconds() {
		long difference = timeBudget - elapsed();
		return (long) (difference / 1.0E6);
	}

	public double remainingTimePercent() {
		return (remainingTime() * 100.0d / timeBudget);
	}

	public void setTimeBudgetMilliseconds(long budget) {
		timeBudget = (long) (budget * 1.0E6);
	}
}
