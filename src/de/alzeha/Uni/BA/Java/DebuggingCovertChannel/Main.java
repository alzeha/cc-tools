package de.alzeha.Uni.BA.Java.DebuggingCovertChannel;

public class Main {
	protected String printOfReceiver;
	protected String printOfSender;

	protected boolean remote;
	
	public Main(Boolean remote) {
		this.remote = remote;
	}
	
	public String getPrintOfSender() {
		return printOfSender;
	}
	
	public String getPrintOfReceiver() {
		return printOfReceiver;
	}
	
	public void startCovertChannel() {
		SenderStart sender = new SenderStart("/home/alli/Desktop/ccnoise/covertChannelUsingTwoProcessesMeasuringSingleAccesses/sender/");
		ReceiverStart receiver = new ReceiverStart("/home/alli/Desktop/ccnoise/covertChannelUsingTwoProcessesMeasuringSingleAccesses/receiver/");
		sender.createExecutable();
		receiver.createExecutable();
		receiver.start();
		sender.start();
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sender.tidyUp();
		receiver.tidyUp();
		printOfReceiver = receiver.getAnswer();
		printOfSender = sender.getAnswer();
	}
	
}
