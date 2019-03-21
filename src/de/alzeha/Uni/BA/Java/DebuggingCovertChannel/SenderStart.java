package de.alzeha.Uni.BA.Java.DebuggingCovertChannel;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/*
 * first, you have to call setPathToSender, otherwise run will fail
 * after that you have to call createExecutable, which will call "make" in the given directory
 * last but not least, you can run the thread
 */
public class SenderStart extends Thread {

	private boolean threadIsRunnable = false;
	private String pathToSender;
	private String answer;
	
	
	public SenderStart(String pathToSender) {
		this.pathToSender = pathToSender;
	}
	
	public String getAnswer() {
		return answer;
	}
	
	private String runCmdAtSenderDir(String cmd) {
		try {
			Process process = Runtime.getRuntime().exec(cmd, null, new File(pathToSender));
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
		runCmdAtSenderDir("make");
	}
	
	public void tidyUp() {
		runCmdAtSenderDir("make clean");
	}
	
	@Override
	public void run() {
		// first, check if this was correctly initialised
		if (threadIsRunnable) {
			answer = runCmdAtSenderDir("./senderMain");
		}
		else {
			System.err.println("The binaries were not created");
		}

	}

}
