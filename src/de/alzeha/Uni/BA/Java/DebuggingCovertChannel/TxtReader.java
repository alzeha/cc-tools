package de.alzeha.Uni.BA.Java.DebuggingCovertChannel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class TxtReader extends Main {

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
	
	
	@Override
	public void startCovertChannel() {
		System.out.println("start reading receive file");
		printOfReceiver = readFile("/home/alli/Desktop/ccnoise/covertChannelUsingTwoProcessesMeasuringSingleAccesses/receiver.txt");
		System.out.println("start reading send file");
		printOfSender = readFile("/home/alli/Desktop/ccnoise/covertChannelUsingTwoProcessesMeasuringSingleAccesses/sender.txt");
		System.out.println("end reading files");
		
	}
	
}
