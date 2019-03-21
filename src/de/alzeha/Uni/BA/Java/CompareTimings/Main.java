package de.alzeha.Uni.BA.Java.CompareTimings;

import java.util.Collections;
import java.util.LinkedList;

/*
 * to use this, enable #define PRINT_READ_AND_WRITE_INTERVALS at the config of the covert channel
 */
public class Main {

	private static SendReceiveDataStruct[] limits = new SendReceiveDataStruct[2];
	
	private static LinkedList<SendReceiveDataStruct> senderStructs = new LinkedList<SendReceiveDataStruct>();
	private static LinkedList<SendReceiveDataStruct> receiverStructs = new LinkedList<SendReceiveDataStruct>();
	
	private static LinkedList<SendReceiveDataStruct> resultingList = new LinkedList<SendReceiveDataStruct>();
	

	private static void filterReceiver() {
		LinkedList<SendReceiveDataStruct> helper = new LinkedList<SendReceiveDataStruct>();
		boolean isActualToLarge;
		boolean isActualToSmall;
		int lastToSmall = -1;
		int firstToLarge = -1;
		for(int i = 0; i < receiverStructs.size(); i++) {
			isActualToSmall = (receiverStructs.get(i).compareTo(limits[0]) == -1);
			isActualToLarge = (receiverStructs.get(i).compareTo(limits[1]) == 1);
			
			if(isActualToSmall) {
				lastToSmall = i;
			}
			
			if(isActualToLarge && firstToLarge == -1) {
				firstToLarge = i;
			}
			
			if(!isActualToSmall && !isActualToLarge) {
				helper.add(receiverStructs.get(i));
			}
			
		}
		
		if (firstToLarge != -1) {
			helper.add(receiverStructs.get(firstToLarge));
		}
		
		if (lastToSmall != -1) {
			helper.add(receiverStructs.get(lastToSmall));
		}
		
		receiverStructs = helper;
	}
		
	private static void createSendList(String printOfSender) {
		String lines[] = printOfSender.split("\\r?\\n");
		long actualTime;
		boolean sendBit;
		for(int i = 0; i < lines.length; i++ ) {
			if(lines[i].length() > 14 && lines[i].substring(0, 7).equals("sending")) {
				actualTime = Long.parseUnsignedLong(lines[i].substring(13).replaceAll(" ", ""));
				sendBit = lines[i].substring(7, 10).contains("1");
				senderStructs.add(new SendReceiveDataStruct(true, sendBit, actualTime));
			}
		}
	}
	
	private static void createReceiveList(String printOfReceiver) {
		String lines[] = printOfReceiver.split("\\r?\\n");
		long actualTime;
		boolean sendBit;
		for(int i = 0; i < lines.length; i++ ) {
			if(lines[i].length() > 14 && lines[i].substring(0, 8).equals("received")) {
				actualTime = Long.parseUnsignedLong(lines[i].substring(13).replaceAll(" ", ""));
				sendBit = lines[i].substring(7, 10).contains("1");
				receiverStructs.add(new SendReceiveDataStruct(false, sendBit, actualTime));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		// use the commented code, if you want to get the covert channel started from java
		
//		de.alzeha.Uni.BA.Java.DebuggingCovertChannel.Main cc = new de.alzeha.Uni.BA.Java.DebuggingCovertChannel.Main();
		de.alzeha.Uni.BA.Java.DebuggingCovertChannel.Main cc = new de.alzeha.Uni.BA.Java.DebuggingCovertChannel.TxtReader();
		cc.startCovertChannel();
		
		
		// get the output and sort it
		String printOfReceiver = cc.getPrintOfReceiver();
		String printOfSender = cc.getPrintOfSender();
		createSendList(printOfSender);
		Collections.sort(senderStructs);
		createReceiveList(printOfReceiver);
		Collections.sort(receiverStructs);
		
		// senderStructs is sorted, therefore the limits are the highest and lowest
		limits[0] = senderStructs.getFirst();
		limits[1] = senderStructs.getLast();
		System.out.println("before filtering: " + receiverStructs.size());
		// filter the received
		filterReceiver();
		System.out.println("after filtering: " + receiverStructs.size());
		
		resultingList = (LinkedList<SendReceiveDataStruct>) senderStructs.clone();
		resultingList.addAll((LinkedList<SendReceiveDataStruct>) receiverStructs.clone());
		
		Collections.sort(resultingList);
		for(int i = 0; i < resultingList.size(); i++) {
			System.out.println(resultingList.get(i));
		}
		
	}
	
	
	
	private static class SendReceiveDataStruct implements Comparable<SendReceiveDataStruct> {
		
		private boolean wasFromSender;
		
		private boolean channelBit;
		
		private long time;
		
		public SendReceiveDataStruct(boolean wasFromSender, boolean channelBit, long time) {
			this.wasFromSender = wasFromSender;
			this.channelBit = channelBit;
			this.time = time;
		}
		
		public boolean getWasFromSender() {
			return wasFromSender;
		}
		
		public boolean getChannelBit() {
			return channelBit;
		}
		
		public long getTime() {
			return time;
		}

		@Override
		public int compareTo(SendReceiveDataStruct arg0) {
			if (this.getTime() < arg0.getTime()) {
				return -1;
			}
			else if (this.getTime() == arg0.getTime()) {
				return 0;
			}
			else {
				return 1;
			}
		}
		
		@Override
		public String toString() {
	        
			String result = "";
			
			if (this.getWasFromSender()) {
				result += "S ";
			}
			else {
				result += "R ";
			}
			
			if(this.getChannelBit()) {
				result += "1 ";
			}
			else {
				result += "0 ";
			}
			
			result += time;
			
			return result;
			
	    }
		
		
	}
	
}
