import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Interpreter {

	public static void read(Process process, Kernel kernel) throws Exception {
		int pc = kernel.getPC(process);
		kernel.incrementPC(process);
		int start = kernel.getStart(process);
		String instruction = Memory.getInstance().getIndex(pc + start);
		String splittedInstruction[] = instruction.split(" ");
		System.out.println("Currently executing process: " + process.toString());
		System.out.println("Currently executing instruction: " + instruction);
		System.out.println();
		if (splittedInstruction[0].equals("print")) {
			String value = kernel.getFromMemory(splittedInstruction[1], process);
			kernel.print(value);
			return;
		}
		if (splittedInstruction[0].equals("writeFile")) {
			String fileName = kernel.getFromMemory(splittedInstruction[1], process);
			String value = kernel.getFromMemory(splittedInstruction[2], process);
			kernel.writeFile(fileName, value);
			return;
		}
		if (splittedInstruction[0].equals("printFromTo")) {
			String lowerBound = kernel.getFromMemory(splittedInstruction[1], process);
			String higherBound = kernel.getFromMemory(splittedInstruction[2], process);
			int low = Integer.parseInt(lowerBound);
			int high = Integer.parseInt(higherBound);
			while (low <= high) {
				kernel.print(low + "");
				low++;
			}
			return;
		}
		if (splittedInstruction[0].equals("input")) {
			String in = kernel.input();
			return;
		}
		if (splittedInstruction[0].equals("readFile")) {
			String fileName = kernel.getFromMemory(splittedInstruction[1], process);
			String in = kernel.readFile(fileName);
			return;
		}
		if (splittedInstruction[0].equals("assign")) {
			String value;
			if (splittedInstruction[2].equals("input")) {
				value = kernel.input();
			} else if (splittedInstruction[2].equals("readFile")) {
				String fileName = kernel.getFromMemory(splittedInstruction[3], process);
				value = kernel.readFile(fileName);
			} else {
				value = kernel.getFromMemory(splittedInstruction[2], process);
				if (value == null)
					value = splittedInstruction[2];

			}
			kernel.assign(splittedInstruction[1], value, process);
			return;

		}
		if (splittedInstruction[0].equals("semWait")) {
			kernel.semWait(splittedInstruction[1], process);
			return;
		}
		if (splittedInstruction[0].equals("semSignal")) {
			kernel.semSignal(splittedInstruction[1], process);
			return;
		}
	}
}
