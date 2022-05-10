import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Interpreter {

	public static void read(Process process, Kernel kernel) throws IOException {
		if (process.delayedInstruction != null) {
			System.out.println("Currently executing process: " + process.toString());
			String splittedInstruction[] = process.delayedInstruction.split(" ");
			System.out.println("Currently executing instruction: " + splittedInstruction[0] + " "
					+ splittedInstruction[1] + " " + process.delayedInput);
			kernel.assign(splittedInstruction[1], process.delayedInput,process);
			process.delayedInput = null;
			process.delayedInstruction = null;
			return;
		}
		String instruction = process.processReader.readLine();
		String splittedInstruction[] = instruction.split(" ");
		if (splittedInstruction[0].equals("assign") && splittedInstruction[2].equals("input")) {
			process.delayedInstruction = instruction;
			instruction = "input";
			splittedInstruction = instruction.split(" ");
		}
		if (splittedInstruction[0].equals("assign") && splittedInstruction[2].equals("readFile")) {
			process.delayedInstruction = instruction;
			instruction = "readFile " + splittedInstruction[3];
			splittedInstruction = instruction.split(" ");
		}
		System.out.println("Currently executing process: " + process.toString());
		System.out.println("Currently executing instruction: " + instruction);
		if (splittedInstruction[0].equals("print")) {
			String value = kernel.getFromMemory(splittedInstruction[1],process);
			kernel.print(value);
			return;
		}
		if (splittedInstruction[0].equals("writeFile")) {
			String fileName = kernel.getFromMemory(splittedInstruction[1],process);
			String value = kernel.getFromMemory(splittedInstruction[2],process);
			kernel.writeFile(fileName, value);
			return;
		}
		if (splittedInstruction[0].equals("printFromTo")) {
			String lowerBound = kernel.getFromMemory(splittedInstruction[1],process);
			String higherBound = kernel.getFromMemory(splittedInstruction[2],process);
			kernel.printFromTo(lowerBound, higherBound);
			return;
		}
		if (splittedInstruction[0].equals("input")) {
			String in = kernel.input();
			if (process.delayedInstruction != null)
				process.delayedInput = in;
			return;
		}
		if (splittedInstruction[0].equals("readFile")) {
			String fileName = kernel.getFromMemory(splittedInstruction[1],process);
			String in = kernel.readFile(fileName);
			if (process.delayedInstruction != null)
				process.delayedInput = in;
			return;
		}
		if (splittedInstruction[0].equals("assign")) {
			String value = kernel.getFromMemory(splittedInstruction[2],process);
			if (value != null)
				kernel.assign(splittedInstruction[1], value,process);
			else
				kernel.assign(splittedInstruction[1], splittedInstruction[2],process);
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
