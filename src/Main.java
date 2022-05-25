import java.io.*;
import java.util.*;

public class Main {

	public static void main(String[] args) throws Exception {
		int timeSlice = 2;
		int arrivalTime1 = 0;
		int arrivalTime2 = 1;
		int arrivalTime3 = 4;
		String processName1 = "Program_1.txt";
		String processName2 = "Program_2.txt";
		String processName3 = "Program_3.txt";
		Process process1 = new Process(arrivalTime1, processName1);
		Process process2 = new Process(arrivalTime2, processName2);
		Process process3 = new Process(arrivalTime3, processName3);
		PriorityQueue<Process> processes = new PriorityQueue<Process>((x, y) -> (x.arrivalTime - y.arrivalTime));
		processes.add(process1);
		processes.add(process2);
		processes.add(process3);
		Scheduler.run(timeSlice, processes, new Kernel());
	}
}
