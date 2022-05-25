import java.io.IOException;
import java.util.*;

public class Mutex {
	Queue<Process> blockedQueue;
	Integer acquiringProcessId;
	Kernel kernel;

	public Mutex(Kernel kernel) {
		blockedQueue = new LinkedList<Process>();
		acquiringProcessId = null;
		this.kernel = kernel;
	}

	public Process semSignal(int signalingProcessId) throws Exception {
		if (signalingProcessId != acquiringProcessId) {
			return null;
		}
		acquiringProcessId = null;
		if (!blockedQueue.isEmpty()) {
			Process unblockedProcess = blockedQueue.poll();
			acquiringProcessId = unblockedProcess.id;
			kernel.setBlocked(unblockedProcess, false);
			while (Scheduler.blockedQueue.peek() != unblockedProcess)
				Scheduler.blockedQueue.add(Scheduler.blockedQueue.poll());
			Scheduler.blockedQueue.poll();
			return unblockedProcess;
		}
		return null;
	}

	public boolean semWait(Process waitingProcess) {
		if (acquiringProcessId == null) {
			acquiringProcessId = waitingProcess.id;
			return false;
		} else {
			blockedQueue.add(waitingProcess);
			return true;
		}
	}

}
