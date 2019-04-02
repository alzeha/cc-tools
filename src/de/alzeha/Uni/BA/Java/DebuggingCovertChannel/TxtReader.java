package de.alzeha.Uni.BA.Java.DebuggingCovertChannel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class TxtReader {

	private String printOfReceiver;
	private String printOfSender;
	

	private String nameOfProject;
	
	private boolean remote;

	public TxtReader(boolean remote) {
		this.remote = remote;
	}
	
	public TxtReader(boolean remote, String nameOfProject) {
		this.remote = remote;
		this.nameOfProject = nameOfProject;
	}
	
	public String getPrintOfSender() {
		return printOfSender;
	}
	
	public String getPrintOfReceiver() {
		return printOfReceiver;
	}
	
	private String readFile(String path) {
		String result = "";
		BufferedReader reader;
		try {
			File file = new File(path);
			reader = new BufferedReader(new FileReader(file));

			String line;
			int i = 0;
			while ((line = reader.readLine()) != null) {
				result += line + '\n';
				i++;
				if (i%500 == 0) {
					System.out.println("read " + i + " lines");
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
		
	}
	
	public void startCovertChannel() {
		if (remote) {
			try {
				File home = new File("/home/alzeha/Desktop/");
				
				File senderTxt = new File("/home/alzeha/Desktop/sender.txt");
				senderTxt.delete();
				
				File receiverTxt = new File("/home/alzeha/Desktop/receiver.txt");
				receiverTxt.delete();
				
				Runtime.getRuntime().exec("scp alli@deeds:/home/alli/Desktop/ccnoise/" + nameOfProject + "/receiver.txt .", null, home);

				Runtime.getRuntime().exec("scp alli@deeds:/home/alli/Desktop/ccnoise/" +nameOfProject + "/sender.txt .", null, home);
				Thread.sleep(1000);
				printOfReceiver = readFile("/home/alzeha/Desktop/receiver.txt");
				printOfSender = readFile("/home/alzeha/Desktop/sender.txt");

			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		else {
			printOfReceiver = readFile("/home/alli/Desktop/ccnoise/covertChannelUsingTwoProcessesMeasuringSingleAccesses/receiver.txt");
			printOfSender = readFile("/home/alli/Desktop/ccnoise/covertChannelUsingTwoProcessesMeasuringSingleAccesses/sender.txt");
		}
	}
	
}
