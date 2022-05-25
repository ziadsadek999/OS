import java.io.*;
import java.util.*;

public class Kernel {
	private Mutex userInput;
	private Mutex userOutput;
	private Mutex file;

	public Kernel() {
		userInput = new Mutex(this);
		userOutput = new Mutex(this);
		file = new Mutex(this);
	}

	public void semWait(String mutexType, Process process) throws IOException {
		boolean isBlocked;
		if (mutexType.equals("userInput")) {
			isBlocked = userInput.semWait(process);
		} else if (mutexType.equals("userOutput")) {
			isBlocked = userOutput.semWait(process);
		} else {
			isBlocked = file.semWait(process);
		}
		if (isBlocked)
			Scheduler.blockedQueue.add(process);
		setBlocked(process, isBlocked);
	}

	public void semSignal(String mutexType, Process process) throws Exception {
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
		System.out.println();
	}

	public void writeFile(String fileName, String value) throws IOException {
		FileWriter writer = new FileWriter(fileName, true);
		writer.write(value + '\n');
		writer.flush();
		writer.close();
	}

	public String readFile(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		StringBuilder file = new StringBuilder();
		while (reader.ready()) {
			file.append(reader.readLine());
			if (reader.ready())
				file.append('\n');
		}
		return file.toString();
	}

	public String input() throws IOException {
		System.out.println("Please enter a value");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		return reader.readLine();
	}

	public int getPC(Process process) {
		String s = Memory.getInstance().getIndex(process.startPosition + 2);
		String split[] = s.split(" ");
		return Integer.parseInt(split[1]);
	}

	public int getStart(Process process) {

		return process.startPosition;
	}

	public boolean isBlocked(Process process) {
		String s = Memory.getInstance().getIndex(process.startPosition + 2);
		String split[] = s.split(" ");
		return split[1].equals("Blocked");
	}

	public boolean isFinished(Process process) {
		int pc = getPC(process);
		String s = Memory.getInstance().getIndex(process.startPosition);
		int end = Integer.parseInt(s.split(" ")[2]);
		if (pc + process.startPosition > end) {
			Memory.getInstance().write(process.startPosition + 3, "Status: finished");
			return true;
		}
		return false;
	}

	public void incrementPC(Process process) {
		int pc = getPC(process);
		Memory.getInstance().write(process.startPosition + 2, "PC: " + (pc + 1));
	}

	public void assign(String variable, String value, Process process) {
		if (getFromMemory(variable, process) == null) {
			String var = Memory.getInstance().getIndex(process.startPosition + 4);
			if (var.equals(null)) {
				Memory.getInstance().write(process.startPosition + 4, "Variable: " + variable + " Value: " + value);
				return;
			}
			var = Memory.getInstance().getIndex(process.startPosition + 5);
			if (var.equals(null)) {
				Memory.getInstance().write(process.startPosition + 5, "Variable: " + variable + " Value: " + value);
				return;
			} else {
				Memory.getInstance().write(process.startPosition + 6, "Variable: " + variable + " Value: " + value);
				return;
			}
		} else {
			String var = Memory.getInstance().getIndex(process.startPosition + 4);
			if (var.equals(variable)) {
				Memory.getInstance().write(process.startPosition + 4, "Variable: " + variable + " Value: " + value);
				return;
			}
			var = Memory.getInstance().getIndex(process.startPosition + 5);
			if (var.equals(variable)) {
				Memory.getInstance().write(process.startPosition + 5, "Variable: " + variable + " Value: " + value);
				return;
			} else {
				Memory.getInstance().write(process.startPosition + 6, "Variable: " + variable + " Value: " + value);
				return;
			}
		}

	}

	public String getFromMemory(String variable, Process process) {
		String var = Memory.getInstance().getIndex(process.startPosition + 4);
		var = var == null ? null : (var.split(" "))[1];
		if (variable.equals(var))
			return (var.split(" "))[3];
		var = Memory.getInstance().getIndex(process.startPosition + 5);
		var = var == null ? null : (var.split(" "))[1];
		if (variable.equals(var))
			return (var.split(" "))[3];
		var = Memory.getInstance().getIndex(process.startPosition + 6);
		var = var == null ? null : (var.split(" "))[1];
		if (variable.equals(var))
			return (var.split(" "))[3];
		return null;

	}

	public void setBlocked(Process process, boolean value) throws IOException {
		if (value) {
			Memory.getInstance().write(process.startPosition + 3, "Status: Blocked");
		} else {
			if (process.startPosition != -1) {
				Memory.getInstance().write(process.startPosition + 3, "Status: Ready");
			} else {
				BufferedReader reader = new BufferedReader(new FileReader("Disk_" + process.name));
				StringBuilder out = new StringBuilder();
				int c = 0;
				while (reader.ready()) {
					if (c != 3) {
						out.append(reader.readLine());
						if (reader.ready()) {
							out.append('\n');
						}
					} else {
						out.append("Status: Ready");
						if (reader.ready()) {
							out.append('\n');
						}
					}
					c++;
				}
				PrintWriter pw = new PrintWriter(new File("Disk_" + process.name));
				pw.println(out);
				pw.flush();
				pw.close();
			}
		}
	}

	public void writeToDisk(Process process) throws FileNotFoundException {
		String[] boundaries = Memory.getInstance().getIndex(process.startPosition).split(" ");
		int end = Integer.parseInt(boundaries[2]);
		PrintWriter writer = new PrintWriter(new File("Disk_" + process.name));
		for (int i = process.startPosition; i <= end; i++) {
			writer.println(Memory.getInstance().getIndex(i));
		}
		Memory.getInstance().positions[process.id - 1][0] = -1;
		process.startPosition = -1;
		writer.flush();
	}

	public void writeToMemory(Process process, int start) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader("Disk_" + process.name));
		Memory.getInstance().positions[process.id - 1][0] = start;
		process.startPosition = start;
		Memory.getInstance().write(start,
				"Boundaries: " + start + " " + (start + Memory.getInstance().positions[process.id - 1][1] - 1));
		reader.readLine();
		start++;
		while (reader.ready()) {
			Memory.getInstance().write(start++, reader.readLine());

		}

	}

	public void createProcess(Process process) throws Exception {
		int id = Memory.getInstance().addToMemory(this, process);
		if (id != -1) {
			process.id = id;
			return;
		}
		Process removed = getRemovedProcess();
		if (removed != null) {
			writeToDisk(removed);
			createProcess(process);
			return;
		}
		Memory.getInstance().reorganize();
		Memory.getInstance().addToMemory(this, process);
		process.id = id;

	}

	public void getToMemory(Process process) throws Exception {
		if (process.startPosition == -1) {
			String[] parsedProcess = readProcessFromDisk("Disk_" + process.name);
			int start = Memory.getInstance().checkFreeSpace(parsedProcess.length);
			while (start == -1) {
				Process removed = getRemovedProcess();
				writeToDisk(removed);
				start = Memory.getInstance().checkFreeSpace(parsedProcess.length);
			}
			writeToMemory(process, start);
		}
		Memory.getInstance().write(process.startPosition + 3, "Status: Running");

	}

	public void setReady(Process process) {
		Memory.getInstance().write(process.startPosition + 3, "Status: Ready");
	}

	public Process getRemovedProcess() {
		Process result = null;
		if (!Scheduler.blockedQueue.isEmpty()) {
			result = Scheduler.blockedQueue.peek();
		} else if (!Scheduler.readyQueue.isEmpty()) {
			result = Scheduler.readyQueue.peek();
		}
		return result;
	}

	public String[] readProcessFromDisk(String name) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(name));
		ArrayList<String> instructions = new ArrayList<>();
		while (reader.ready()) {
			instructions.add(reader.readLine());
		}
		String[] result = new String[instructions.size()];
		for (int i = 0; i < instructions.size(); i++) {
			result[i] = instructions.get(i);
		}
		return result;
	}

}
