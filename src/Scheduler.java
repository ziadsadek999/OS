import java.io.*;
import java.util.*;

public class Scheduler {
	static Queue<Process> readyQueue;
	static Queue<Process> blockedQueue;

	public static void run(int timeSlice, PriorityQueue<Process> processes, Kernel kernel) throws Exception {
		int cycle = 0;
		Process currentlyExecuting = null;
		readyQueue = new LinkedList<Process>();
		blockedQueue = new LinkedList<Process>();
		int remainingTime = timeSlice;
		while (!readyQueue.isEmpty() || !processes.isEmpty() || currentlyExecuting != null) {
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
			System.out.println("Cycle: " + (cycle));
			while (!processes.isEmpty() && processes.peek().arrivalTime == cycle) {
				readyQueue.add(processes.peek());
				kernel.createProcess(processes.poll());
			}
			
			if (currentlyExecuting == null) {
				if (!readyQueue.isEmpty()) {
					currentlyExecuting = readyQueue.poll();
					kernel.getToMemory(currentlyExecuting);
					
					System.out.println(Memory.getInstance());
					cycle++;
					Interpreter.read(currentlyExecuting, kernel);
					remainingTime--;
				}
			} else {
				if (kernel.isBlocked(currentlyExecuting) || kernel.isFinished(currentlyExecuting)) {
					if (!readyQueue.isEmpty()) {
						currentlyExecuting = readyQueue.poll();
						kernel.getToMemory(currentlyExecuting);
					} else {
						currentlyExecuting = null;
					}
					remainingTime = timeSlice;
				} else if (remainingTime == 0) {
					remainingTime = timeSlice;
					readyQueue.add(currentlyExecuting);
					kernel.setReady(currentlyExecuting);
					if (!readyQueue.isEmpty()) {
						currentlyExecuting = readyQueue.poll();
						kernel.getToMemory(currentlyExecuting);
					} else {
						currentlyExecuting = null;
					}
				}
				if (currentlyExecuting != null) {
					System.out.println(Memory.getInstance());
					cycle++;
					Interpreter.read(currentlyExecuting, kernel);
					remainingTime--;
				}else {
					System.out.println(Memory.getInstance());
					cycle++;
				}
			}
		}

	}

}
