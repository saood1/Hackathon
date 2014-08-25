package com.hackathon.socket.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.json.*;

import com.hackathon.filesync.CommonUtility;
import com.hackathon.filesync.Constants;

public class Client {

	public static void main(String[] args) throws IOException, InterruptedException, JSONException {
		
		//Start the client socket
		Boolean isClientRunning = true;
		Thread socket = new Thread(){
			public void run(){
				try {
					try {
						CommonUtility.getInstance().startSocket(null);
					} 
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				} 
				catch (UnknownHostException e) {
					e.printStackTrace();
				} 
			}
		};
		
		socket.start();
		CommonUtility.getInstance().initializeFileSync(false);
		
		/*
		 * Terminal Logic
		 * 
		 * 1. Share file
		 * 2. Update co-ordinates
		 * 3. online/offilne mock
		 * 4. help
		 * 5. quit
		 */
		System.out.println("Client is UP......");
		printHelpMenu();
		
		do
		{
			System.out.print("Enter command : ");
			BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
			String cmd = buf.readLine();
			
			//command --> share <file_name> <user_id>
			if(cmd.startsWith("share")){
				shareFile(cmd);
			}
			
			//command --> updateLoc
			else if(cmd.startsWith("updateLoc")){
				updateClientLoc();
			}
			
			//command --> updateState [true/false]
			else if(cmd.startsWith("updateState")){
				updateClientState(cmd);
			}
			
			else if(cmd.startsWith("help")){
				printHelpMenu();
			}
				
			else if (cmd.startsWith("quit")){
				isClientRunning = false;
			}

			System.out.println("isClientRunning : " +  isClientRunning);
		}while(isClientRunning);
	}

	/**
	 * Re-set the geo-cordinates
	 * @throws UnknownHostException
	 * @throws JSONException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void updateClientLoc() throws UnknownHostException, JSONException, IOException, InterruptedException {
		CommonUtility.getInstance().setMyGeoCoordinates(CommonUtility.getInstance().getMyGeoCordinates());
		CommonUtility.getInstance().sendUpdatedClientInfoToServer();
	}

	/**
	 * @param line
	 * @throws UnknownHostException
	 * @throws JSONException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void updateClientState(String line) throws UnknownHostException, JSONException, IOException,	InterruptedException {
		String[] words = line.split(" ");
		
		if(words.length == 2){
			CommonUtility.getInstance().setClientState(Boolean.parseBoolean(words[1]));
			CommonUtility.getInstance().sendUpdatedClientInfoToServer();
		}
		else{
			System.out.println("Unidentified command ...");
			printHelpMenu();
		}
	}

	/**
	 * Function to print the menu options
	 */
	private static void printHelpMenu() {
		String help = null;
		help = "1. share <file_name> <user_id>";
		help += "\n";
		help += "2. help";
		help += "\n";
		help += "3. quit";
		help += "\n";
		help += "4. updateLoc";
		help += "\n";
		help += "5. updateState [true/false]";
		help += "\n";
		System.out.println(help);
	}

	/**
	 * Function to accept the share command and process
	 * @param line
	 * @throws JSONException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void shareFile(String line) throws JSONException, IOException, InterruptedException {
		String[] words = line.split(" ");
		
		if(words.length == 3){
			JSONObject sh = new JSONObject();
			sh.put(Constants.FILE_NAME, words[1].toString());
			sh.put(Constants.RECIPIENT_USER_ID, words[2].toString());
			CommonUtility.getInstance().sendFileShareRequestToServer(sh.toString());
		}
		else{
			System.out.println("Unidentified command ...");
			printHelpMenu();
		}
	}
}


