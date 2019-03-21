package de.alzeha.Uni.BA.Java.DebuggingCovertChannel;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class ReceiverStart extends Thread {

	private boolean threadIsRunnable = false;
	private String pathToReceiver;
	private String answer;

	
	public ReceiverStart(String pathToReceiver) {
		this.pathToReceiver = pathToReceiver;
	}
	
	public String getAnswer() {
		return answer;
	}
	
	
	private String runCmdAtReceiverDir(String cmd) {
		try {
			Process process = Runtime.getRuntime().exec(cmd, null, new File(pathToReceiver));
			String output = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String answer;
			while ((answer = reader.readLine()) != null) {
				output += (answer + "\n");
			}

			if(process.waitFor() == 0) {
				threadIsRunnable = true;
			}
			else {
				System.out.println("Something went wrong, here is the output");	
				System.out.println(output);
			}
			return output;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
		
		
	}
	
	public void createExecutable() {
		runCmdAtReceiverDir("make");
	}
	
	public void tidyUp() {
		runCmdAtReceiverDir("make clean");
	}
	
	@Override
	public void run() {
		// first, check if this was correctly initialised
		if (threadIsRunnable) {
			answer = runCmdAtReceiverDir("./receiverMain");
		}
		else {
			System.err.println("The binaries were not created");
		}

	}
}
