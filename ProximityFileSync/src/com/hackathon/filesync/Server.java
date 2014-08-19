package com.hackathon.filesync;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Server {

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		Thread socketServer = new Thread(){
			public void run(){
				CommonUtility.startSocket(Constants.SERVER_PORT_NO);		
			}
		};
		
		socketServer.start();
		
		//This is a test function to check sending and receiving a file on the client side.
		TimeUnit.SECONDS.sleep(5);
		CommonUtility.sendFile("127.0.0.1", Constants.SERVER_PORT_NO, "/Users/mohamed.khan/AutoUpdater.log");
	}
}
