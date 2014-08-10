package com.hackathon.filesync;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Server {

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		final Server s = new Server();
		Thread server = new Thread(){
			public void run(){
				s.startServerSocket(Constants.PORT_NO);		
			}
		};
		
		server.start();
		
		//This is a test function to check sending and recieving a file on the client side.
		//CommonUtility.sendFile("127.0.0.1", Constants.PORT_NO, "/Users/mohamed.khan/AutoUpdater.log");
	}
	
	private void startServerSocket(int portNo){
		try {
			ServerSocket serverSocket = new ServerSocket(portNo);
			
			try{
				while(true){
					Socket socket = serverSocket.accept(); 
					System.out.println("Accepted connection : " + socket); 
					
					ObjectInputStream objInp = new ObjectInputStream(socket.getInputStream());
					HashMap<String, byte[]> infoMap = (HashMap<String, byte[]>)objInp.readObject();
					
					String tasks = new String(infoMap.get("string"));
					/*
					 * This block will contain the logic to parse the string which
					 * will be a json string
					 * 
					 * 
					 */
					System.out.println(tasks);
					
					
					/*
					 * This block will switch cases or conditional checks to execute tasks based on there nature
					 * will be a json string
					 * 
					 * 
					 * 
					 */
					
					byte fileBytes[] = infoMap.get("file");
					if(fileBytes!=null && fileBytes.length>0)
						CommonUtility.recieveFile(fileBytes, "test.log");
					
					objInp.close();
				}	
			}
			catch (IOException e){
				System.out.println("An IOException occured " + e.getMessage());
			}
			finally{
				serverSocket.close();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
