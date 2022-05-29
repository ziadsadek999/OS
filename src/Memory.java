import java.io.*;
import java.util.*;

public class Memory {
	final public static Memory instance = new Memory();
	String[] memory = new String[40];
	int idCounter = 1;
	int[][] positions = new int[3][2];

	private Memory() {
		positions[0] = new int[] { -1, -1 };
		positions[1] = new int[] { -1, -1 };
		positions[2] = new int[] { -1, -1 };
	}

	public static Memory getInstance() {
		return instance;
	}


	public String getIndex(int i) {
		return memory[i];
	}

	public int checkFreeSpace(int size) {
		PriorityQueue<int[]> pq = new PriorityQueue<int[]>((x, y) -> x[0] - y[0]);
		for (int i = 0; i < 3; i++) {
			if (positions[i][0] != -1) {
				pq.add(new int[] { positions[i][0], positions[i][1] });
			}
		}
		if (pq.isEmpty())
			return 0;
		int hole = pq.peek()[0];
		if (hole >= size)
			return 0;
		while (pq.size() > 1) {
			int[] curr = pq.poll();
			int start = curr[0] + curr[1];
			hole = pq.peek()[0] - start;
			if (hole >= size)
				return start;
		}
		int[] curr = pq.poll();
		int start = curr[0] + curr[1];
		hole = 40 - start;
		if (hole >= size)
			return start;
		return -1;

	}

	public int addToMemory(Kernel kernel,Process process,String[] instructions) throws Exception {
		int size = instructions.length + 7;
		int start = checkFreeSpace(size);
		if (start == -1) {
			return -1;
		}
		process.startPosition = start;
		positions[idCounter - 1][0] = start;
		positions[idCounter - 1][1] = size;
		memory[start] = "Boundaries: " + start + " " + (start + size - 1);
		start++;
		memory[start++] = "Process id: " + idCounter;
		memory[start++] = "PC: 7";
		memory[start++] = "Status: Ready";
		memory[start++] = null;
		memory[start++] = null;
		memory[start++] = null;
		for (int i = 0; i < instructions.length; i++) {
			memory[start++] = instructions[i];
		}
		return idCounter++;
	}

	public void reorganize() {
		int process;
		if (positions[0][0] != -1) {
			process = 0;
		} else if (positions[1][0] != -1) {
			process = 1;
		} else {
			process = 2;
		}
		int size = positions[process][1];
		int start = positions[process][0];
		write(0, "Boundaries: 0 " + (size - 1));
		start++;
		for (int i = 1; i < size; i++) {
			write(i, memory[start++]);
		}
	}

	public void write(int index, String val) {
		memory[index] = val;
	}

	public String toString() {
		StringBuilder out = new StringBuilder();
		int i = 0;
		while (i < memory.length) {
			out.append("Address: " + i + " " + memory[i++]);
			out.append('\n');
		}
		return out.toString();
	}
}
