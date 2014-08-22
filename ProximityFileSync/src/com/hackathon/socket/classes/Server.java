package com.hackathon.socket.classes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;

import com.hackathon.filesync.CommonUtility;
import com.hackathon.filesync.Constants;

public class Server {

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException, JSONException {
		Thread socketServer = new Thread(){
			public void run(){
				try {
					CommonUtility.getInstance().startSocket(Constants.SERVER_PORT_NO);
				} 
				catch (UnknownHostException e) {
					e.printStackTrace();
				}		
			}
		};
		
		socketServer.start();
		
		TimeUnit.SECONDS.sleep(5);
		CommonUtility.getInstance().initializeFileSync();
		
		//This is a test function to check sending and receiving a file on the client side.
		//TimeUnit.SECONDS.sleep(5);
		//CommonUtility.sendFile("127.0.0.1", Constants.SERVER_PORT_NO, "/Users/mohamed.khan/AutoUpdater.log");
	}
}
