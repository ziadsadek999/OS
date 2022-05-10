import java.io.*;
import java.util.*;

public class Scheduler {
	static Queue<Process> readyQueue;
	static Queue<Process> blockedQueue;
	public static void run(int timeSlice, PriorityQueue<Process> processes,Kernel kernel) throws IOException {
		int currentTime = processes.peek().arrivalTime;
		readyQueue = new LinkedList<Process>();
		blockedQueue = new LinkedList<Process>();
		readyQueue.add(processes.poll());
		int remainingTime = timeSlice;
		Process currentlyExecuting = readyQueue.poll();
		printQueues();
		while (!readyQueue.isEmpty() || !processes.isEmpty() || currentlyExecuting != null) {
			while (!processes.isEmpty() && currentTime == processes.peek().arrivalTime) {
				readyQueue.add(processes.poll());
			}
			if (currentlyExecuting == null && !readyQueue.isEmpty()) {
				currentlyExecuting = readyQueue.poll();
				printQueues();
				remainingTime = timeSlice;
			}
			if (currentlyExecuting != null) {
				Interpreter.read(currentlyExecuting,kernel);
				if (currentlyExecuting.isBlocked || !currentlyExecuting.processReader.ready()) {
					if (currentlyExecuting.isBlocked) System.out.println(currentlyExecuting+" got blocked!");
					else System.out.println(currentlyExecuting+" finished it's execution!");
					printQueues();
					System.out.println();
					if (!readyQueue.isEmpty()) {
						currentlyExecuting = readyQueue.poll();
						printQueues();
						
					}
					else
						currentlyExecuting = null;
					remainingTime = timeSlice;
				} else {
					remainingTime--;
					if (remainingTime == 0) {
						readyQueue.add(currentlyExecuting);
						currentlyExecuting = readyQueue.poll();
						printQueues();
						remainingTime = timeSlice;
					}
				}
			}
			currentTime++;
		}

	}
	public static void printQueues() {
		System.out.println("Ready queue: "+readyQueue);
		System.out.println("Blocked queue: "+blockedQueue);
	}
}
