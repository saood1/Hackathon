package com.hackathon.socket.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.json.*;

import com.hackathon.filesync.CommonUtility;
import com.hackathon.filesync.Constants;
import com.infomatiq.jsi.Point;

public class Client {

	public static void main(String[] args) throws IOException, InterruptedException, JSONException {
		
		//Start the client socket
		Boolean isClientRunning = true;
		Thread socket = new Thread(){
			public void run(){
				try {
					try {
						CommonUtility.getInstance().startSocket(false);
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
		
		//Start client socket
		socket.start();
		
		//Initialize client socket
		TimeUnit.SECONDS.sleep(5);
		CommonUtility.getInstance().initializeFileSync();
	//comment	
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
			System.out.print("Enter next command : ");
			BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
			String cmd = buf.readLine();
			
			//command --> share <file_name> <user_id>
			if(cmd.startsWith("share")){
				shareFile(cmd);
			}
			
			//command --> updateLoc
			//command --> updateLoc 5 10
			else if(cmd.startsWith("updateLoc")){
				updateClientLoc(cmd);
			}
			
			//command --> updateState [true/false]
			else if(cmd.startsWith("updateState")){
				updateClientState(cmd);
			}
			
			else if(cmd.startsWith("fileList"))
			{
				displayLocalFiles();
			}
			
			else if(cmd.startsWith("help")){
				printHelpMenu();
			}
				
			else if (cmd.startsWith("quit")){
				isClientRunning = false;
			}

			//System.out.println("isClientRunning : " +  isClientRunning);
		}while(isClientRunning);
	}

	private static void displayLocalFiles() throws UnknownHostException {
		CommonUtility.getInstance().refreshFileList();
		System.out.println("Local file list : ");
		CommonUtility.getInstance().displayFileList();
	}

	/**
	 * Re-set the geo-cordinates
	 * @throws UnknownHostException
	 * @throws JSONException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void updateClientLoc(String line) throws UnknownHostException, JSONException, IOException, InterruptedException {
		
		CommonUtility.getInstance().refreshFileList();
		
		String[] words =  line.split(" ");
		
		if(words.length == 1)
		{
			CommonUtility.getInstance().setMyGeoCoordinates(CommonUtility.getInstance().getMyGeoCordinates());
			CommonUtility.getInstance().sendUpdatedClientInfoToServer();
		}
		else if(words.length == 3)
		{
			Point p = new Point(Integer.parseInt(words[1]) , Integer.parseInt(words[2]));
			CommonUtility.getInstance().setMyGeoCoordinates(p);
			CommonUtility.getInstance().sendUpdatedClientInfoToServer();
		}
		else
		{
			System.out.println("Format of updateState command is erroneous ..");
			printHelpMenu();
		}
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
			CommonUtility.getInstance().refreshFileList();
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
		help += "4. updateLoc or updateLoc x y";
		help += "\n";
		help += "5. updateState [true/false]";
		help += "\n";
		help += "6. fileList";
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
			if(CommonUtility.isFileExists(words[1].toString()))
			{
				JSONObject sh = new JSONObject();
				sh.put(Constants.FILE_NAME, words[1].toString());
				sh.put(Constants.RECIPIENT_USER_ID, words[2].toString());
				CommonUtility.getInstance().sendFileShareRequestToServer(sh.toString());
			}
			else
				System.out.println("File doesnt exists locally ....");
			
		}
		else{
			System.out.println("Unidentified command ...");
			printHelpMenu();
		}
	}
}


