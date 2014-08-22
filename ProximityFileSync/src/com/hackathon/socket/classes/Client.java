package com.hackathon.socket.classes;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.*;

import com.hackathon.filesync.CommonUtility;
import com.hackathon.filesync.Constants;
import com.infomatiq.jsi.Point;

public class Client {

	public static void main(String[] args) throws IOException, InterruptedException, JSONException {
		
		//Start the client socket
		Thread socket = new Thread(){
			public void run(){
				try {
					CommonUtility.getInstance().startSocket(null);
				} 
				catch (UnknownHostException e) {
					e.printStackTrace();
				}		
			}
		};
		
		socket.start();
		CommonUtility.getInstance().initializeFileSync();
	}

}
