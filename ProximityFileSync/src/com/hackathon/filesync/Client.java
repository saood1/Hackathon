package com.hackathon.filesync;

public class Client {

	public static void main(String[] args) {
		Thread socketServer = new Thread(){
			public void run(){
				CommonUtility.startServerSocket(Constants.PORT_NO);		
			}
		};
		
		socketServer.start();
	}

}
