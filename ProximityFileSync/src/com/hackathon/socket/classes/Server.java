package com.hackathon.socket.classes;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;

import com.hackathon.filesync.BootStraper;
import com.hackathon.filesync.CommonUtility;
import com.hackathon.filesync.Constants;

public class Server {

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException, JSONException {
		Thread socketServer = new Thread(){
			public void run(){
				try {
					CommonUtility.getInstance().startSocket(true);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				} 
				catch (IOException e) {
					e.printStackTrace();
				} 
			}
		};
		
		//Start server socket
		socketServer.start();
				
		//Initialize server socket
		TimeUnit.SECONDS.sleep(5);
		CommonUtility.getInstance().initializeFileSync();
	}
		
}
