package de.alzeha.Uni.BA.Java.CompareTimings;

import java.util.Collections;
import java.util.LinkedList;

/*
 * to use this, enable #define PRINT_READ_AND_WRITE_INTERVALS at the config of the covert channel
 */
public class Main {
	
	// variables to configure the tool
	// runCC means, the covert channel shall be run
	private static boolean runCC = false;
	// remote means, we are on a different workstation than the covert channel
	private static boolean remote = true;
	
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
		boolean lastWasReceived = false;
		long actualTime, measuredTime;
		boolean sendBit;
		for(int i = 0; i < lines.length; i++ ) {
			if(lines[i].length() > 14 && lines[i].substring(0, 8).equals("received")) {
				actualTime = Long.parseUnsignedLong(lines[i].substring(13).replaceAll(" ", ""));
				sendBit = lines[i].substring(7, 10).contains("1");
				receiverStructs.add(new SendReceiveDataStruct(false, sendBit, actualTime));
				lastWasReceived = true;
				
			}
			else {
				if (lines[i].length() > 15 && lines[i].substring(0, 13).equals("time measured") && lastWasReceived) {
					measuredTime = Long.parseUnsignedLong( lines[i].substring(15).replaceAll(" ", "") );
					receiverStructs.getLast().setMeasuredTime(measuredTime);
				}
				lastWasReceived = false;
			}
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		de.alzeha.Uni.BA.Java.DebuggingCovertChannel.Main cc;
		if(runCC) {
			cc = new de.alzeha.Uni.BA.Java.DebuggingCovertChannel.Main(remote);
		} else {
			cc = new de.alzeha.Uni.BA.Java.DebuggingCovertChannel.TxtReader(remote);
		}
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
		// filter the received
		filterReceiver();
		
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
		
		private long measuredTime = 0;
		
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

		public long getMeasuredTime() {
			return measuredTime;
		}

		public void setMeasuredTime(long actual) {
			measuredTime = actual;
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
			
			if (!this.getWasFromSender() && this.getMeasuredTime() != 0) {
				result += " in ";
				result += this.getMeasuredTime();
				
			}
			
			return result;
			
	    }
		
		
	}
	
}
