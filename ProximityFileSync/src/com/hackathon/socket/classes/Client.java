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
		
		//Create users shared directory if not already created
		CommonUtility.getInstance().createUserSharedDir(Constants.SHARED_DIR);
		
		//Create client info json string
		String jsonClientInfoString = CommonUtility.getInstance().constructJSONClientInformation();
		System.out.println("JSON String = " + jsonClientInfoString.toString());
		
		//CommonUtility.sendFile("127.0.0.1", "127.0.0.1", portNo, userDirPath + "a.pdf");
		
		//Send the client information to the server
		CommonUtility.getInstance().sendClientInformationToServer(Constants.CLIENT_INFORMATION, jsonClientInfoString);
	}

}
