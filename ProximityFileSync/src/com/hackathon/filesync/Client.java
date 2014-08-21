package com.hackathon.filesync;

import java.io.IOException;
import java.net.UnknownHostException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.infomatiq.jsi.Point;

public class Client {

	public static void main(String[] args) throws IOException, InterruptedException {
		final int portNo = CommonUtility.getMyPortNo();
		final String ipAddress = CommonUtility.getMyIPAddress();
		final Point cord = CommonUtility.getMyGeoCordinates();
		final String userID = CommonUtility.getMyUserID();
		
		//Create users shared directory if not already created
		CommonUtility.createUserSharedDir();
		
		//Create the JSON Object
		JSONObject jo = new JSONObject();
		jo.put(Constants.USER_ID, userID);
		jo.put(Constants.PORT_NO, portNo);
		jo.put(Constants.IP_ADDRESS, ipAddress);
		jo.put(Constants.CORDINATES, cord.x + "|" + cord.y);

		JSONArray ja = new JSONArray();
		ja.add(jo);

		JSONObject mainObj = new JSONObject();
		mainObj.put(Constants.CLIENT_INFORMATION, ja);
		//System.out.println("JSON String = " + mainObj.toString());
		
		//Send the client information to the server
		//CommonUtility.sendClientInformationToServer(mainObj.toString());
		
		//Start the client socket
		Thread socket = new Thread(){
			public void run(){
				CommonUtility.startSocket(portNo);		
			}
		};
		
		socket.start();		
	}

}
