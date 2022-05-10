import java.io.*;
import java.util.*;

public class Main {

	public static void main(String[] args) throws IOException {
		int timeSlice = 2;
		int arrivalTime1 = 0;
		int arrivalTime2 = 1;
		int arrivalTime3 = 4;
		String processName1 = "Program_1.txt";
		String processName2 = "Program_2.txt";
		String processName3 = "Program_3.txt";
		Process process1 = new Process(1, arrivalTime1, processName1);
		Process process2 = new Process(2, arrivalTime2, processName2);
		Process process3 = new Process(3, arrivalTime3, processName3);
		PriorityQueue<Process> processes = new PriorityQueue<Process>((x, y) -> (x.arrivalTime - y.arrivalTime));
		if (process1.processReader.ready())
			processes.add(process1);
		if (process2.processReader.ready())
			processes.add(process2);
		if (process3.processReader.ready())
			processes.add(process3);
		Scheduler.run(timeSlice, processes,new Kernel());
	}
}
