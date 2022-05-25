import java.io.*;
import java.util.*;
public class Process{
	Integer id;
    String name;
    int arrivalTime;
    int startPosition;
    public Process(int arrivalTime,String name) throws IOException{
    	this.name = name;
    	this.arrivalTime = arrivalTime;
    }
    public String toString() {
		return "Process " + id;
	}
}
