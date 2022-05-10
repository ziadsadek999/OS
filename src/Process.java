import java.io.*;
import java.util.*;
public class Process{
    int id;
    String name;
    int arrivalTime;
    BufferedReader processReader;
    boolean isBlocked;
    String delayedInput;
    String delayedInstruction;
    HashMap<String,String> memory;
    public Process(int id,int arrivalTime,String name) throws IOException{
    	this.id = id;
    	this.name = name;
    	this.arrivalTime = arrivalTime;
    	processReader = new BufferedReader(new FileReader(name));
    	isBlocked = false;
    	memory = new HashMap<String, String>();
    }
    public String toString() {
		return "Process " + id;
	}
}
