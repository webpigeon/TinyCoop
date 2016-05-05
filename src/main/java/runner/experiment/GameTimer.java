package runner.experiment;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class GameTimer {

	public static long getCPUTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;
	}

	public static long getUserTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadUserTime() : 0L;
	}

}
