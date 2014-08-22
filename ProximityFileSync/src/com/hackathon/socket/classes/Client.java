package com.hackathon.socket.classes;

import java.io.IOException;

import org.json.*;

import com.hackathon.filesync.CommonUtility;
import com.hackathon.filesync.Constants;
import com.infomatiq.jsi.Point;

public class Client {

	public static void main(String[] args) throws IOException, InterruptedException, JSONException {
		final int portNo = CommonUtility.getMyPortNo();
		final String ipAddress = CommonUtility.getMyIPAddress();
		final Point cord = CommonUtility.getMyGeoCordinates();
		final String userID = CommonUtility.getMyUserID();
		final int uuid = CommonUtility.getMyUserID().hashCode();
		final String userDirPath = Constants.SHARED_DIR;
		
		//Start the client socket
		Thread socket = new Thread(){
			public void run(){
				CommonUtility.startSocket(portNo);		
			}
		};
		socket.start();
		
		//Create users shared directory if not already created
		CommonUtility.createUserSharedDir(userDirPath);
		
		//Create the JSON Object
		JSONObject jo = new JSONObject();
		jo.put(Constants.USER_ID, userID);
		jo.put(Constants.UUID, uuid);
		jo.put(Constants.PORT_NO, portNo);
		jo.put(Constants.IP_ADDRESS, ipAddress);
		jo.put(Constants.CORDINATES, cord.x + "|" + cord.y);

		JSONArray ja = new JSONArray();
		ja.put(jo);

		JSONObject mainObj = new JSONObject();
		mainObj.put(Constants.CLIENT_INFORMATION, ja);
		//System.out.println("JSON String = " + mainObj.toString());
		
		CommonUtility.sendFile("127.0.0.1", "127.0.0.1", portNo, userDirPath + "a.pdf");
		
		//Send the client information to the server
		//CommonUtility.sendClientInformationToServer(mainObj.toString());
		
				
	}

}
