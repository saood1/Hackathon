package com.hackathon.filesync;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


/**
 * This class holds all common functionality that can be used by server and client. 
 * @author mohamed.khan
 *
 */
public class CommonUtility {

	/**
	 * Sends a file to a dedicated host with payload information and  
	 * @param hostIp
	 * @param portNo
	 * @param filePath
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void sendFile(String hostIp, int portNo, String filePath) throws UnknownHostException, IOException, InterruptedException{
		Socket socket = new Socket(hostIp, portNo);
		HashMap<String, byte[]> information = new HashMap<String, byte[]>();
		
		try{
			//Check if the socket connects to the given ip and ports
			while(socket.isConnected()==false){
				System.out.println("Waiting for the socket to open..");
				TimeUnit.SECONDS.sleep(5);
			}
					
			//Add the task to map
			String task = "TASK:1";
			information.put("string", task.getBytes());
			
			//Read the file
			File file = new File(filePath);
		    long length = file.length();
		    		    
		    //Initialize the byte array for transfer
		    byte[] fileBytes = new byte[(int) length];
		    		    
		    //Open a InputStream for reading from file Object
		    FileInputStream fis  = new FileInputStream(file);
		    BufferedInputStream bis = new BufferedInputStream(fis);;
		    bis.read(fileBytes);
		    
		    //Add the file payload to arraylist
		    information.put("file", fileBytes);
		    
		    try{
		    	ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
		    	
		    	//Start transfer
		    	objOut.writeObject(information);
		    	objOut.flush();
				objOut.close();
			}
		    catch (IOException e){
		    	System.out.println(e.getLocalizedMessage());
		    }
			finally{
				fis.close();
			    bis.close();
			}
		}
		finally{
		    socket.close();
		}
	}
	
	/**
	 * Function takes a file byte array and writes it to a file
	 * @param bytes
	 * @param fileName
	 */
	public static void recieveFile(byte[] bytes, String fileName){
		FileOutputStream fos =null;
		BufferedOutputStream bos = null;
		
		try{
			fos = new FileOutputStream("/Users/mohamed.khan/" + fileName);
			bos = new BufferedOutputStream(fos);
			bos.write(bytes);
			bos.flush();
		    bos.close();
		    fos.close();
	    }
		catch (IOException e){
			System.out.println(e.getLocalizedMessage());
		}
    }
}
