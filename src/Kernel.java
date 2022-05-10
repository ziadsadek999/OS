import java.io.*;
import java.util.*;

public class Kernel {
	private Mutex userInput;
	private Mutex userOutput;
	private Mutex file;

	public Kernel() {
		userInput = new Mutex();
		userOutput = new Mutex();
		file = new Mutex();
	}

	public void semWait(String mutexType, Process process) {
		boolean isBlocked;
		if (mutexType.equals("userInput")) {
			isBlocked = userInput.semWait(process);
		} else if (mutexType.equals("userOutput")) {
			isBlocked = userOutput.semWait(process);
		} else {
			isBlocked = file.semWait(process);
		}
		if(isBlocked)
			Scheduler.blockedQueue.add(process);
		process.isBlocked = isBlocked;
	}

	public void semSignal(String mutexType, Process process) {
		Process unblockedProcess;
		if (mutexType.equals("userInput")) {
			unblockedProcess = userInput.semSignal(process.id);
		} else if (mutexType.equals("userOutput")) {
			unblockedProcess = userOutput.semSignal(process.id);
		} else {
			unblockedProcess = file.semSignal(process.id);
		}
		if (unblockedProcess != null)
			Scheduler.readyQueue.add(unblockedProcess);
	}

	public void print(String value) {
		System.out.println(value);
	}

	public void writeFile(String fileName, String value) throws IOException {
		FileWriter writer = new FileWriter(fileName, true);
		writer.write(value + '\n');
		writer.flush();
		writer.close();
	}

	public String readFile(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		return reader.readLine();
	}

	public void printFromTo(String lowerBound, String higherBound) {
		int low = Integer.parseInt(lowerBound);
		int high = Integer.parseInt(higherBound);

		StringBuilder output = new StringBuilder();
		while (low <= high) {
			output.append(low + " ");
			low++;
		}
		System.out.println(output);
	}

	public String getFromMemory(String variable,Process process) {
		return process.memory.getOrDefault(variable, null);
	}

	public String input() throws IOException {
		System.out.println("Please enter a value");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		return reader.readLine();
	}

	public void assign(String variable, String value,Process process) {
		process.memory.put(variable, value);
	}
}
