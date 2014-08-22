package com.hackathon.filesync;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//MongoDB packages
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.infomatiq.jsi.Point;


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
		    
		    //Add the file payload to the map
		    information.put(Constants.FILE, fileBytes);
		    
		    try{
		    	sendBytesThroughSocket(hostIp, portNo, information);
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
	
	/**
	 * Starts the server socket
	 * @param portNo
	 */
	public static void startSocket(Integer portNo){
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
	
	/**
	 * @return Build the JSON string containing the client information when the system boots up for the first time
	 */
	public static String getJSONClientInfo(){
		String val = null;
		return val;
			
	}
	
	/**
	 * @return The ip address of the current system
	 */
	public static String getMyIPAddress() throws UnknownHostException{
		String val = InetAddress.getLocalHost().getHostAddress();
		return val;
	}
	
	/**
	 * @return 5 digit port no which is unused by the system
	 */
	public static int getMyPortNo(){
		int val = getRandomInteger(49152, 65534);
		return val;
	}
	
	/**
	 * @return x,y co-ordinates of the users location
	 */
	public static Point getMyGeoCordinates(){
		return new Point(getRandomInteger(0, 30), getRandomInteger(0, 30));
	}
	
	/**
	 * @return a random integer between the low-high range
	 */
	public static int getRandomInteger(int low, int high){
		return (int) ((Math.random() * (high - low)) + low);
	}
	

	/**
	 * @return the current logged-in used id
	 */
	public static String getMyUserID() {
		return System.getProperty("user.name");
	}
	
	/**
	 * Creates a shared directory under users home directory
	 * @throws IOException
	 */
	public static void createUserSharedDir() throws IOException{
		String homeDir = System.getProperty("user.home");
		File dir = new File(homeDir + Constants.SHARED_DIR);
		
		if(!dir.exists()){
			dir.mkdirs();
		}
		
	}
	
	
	/**
	 * This method sends information to Server for processing
	 * @param jsonString
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void sendClientInformationToServer(String jsonString) throws UnknownHostException, IOException, InterruptedException{
		HashMap<String, byte[]> information = new HashMap<String, byte[]>();
		information.put(Constants.CLIENT_INFORMATION, jsonString.getBytes());
		
		//Start sending the byte information
		sendBytesThroughSocket(Constants.SERVER_IP_ADDRESS, Constants.SERVER_PORT_NO, information);
	}
	
	/**
	 * Send byte information to through the connected socket
	 * @param socket
	 * @param information
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	private static void sendBytesThroughSocket(String hostIP, int portNo, Object information) throws IOException, InterruptedException{
		Socket socket = new Socket(hostIP, portNo);
		
		try{
			//Check if the socket connects to the given ip and ports
			while(socket.isConnected()==false){
				System.out.println("Waiting for the socket to open..");
				TimeUnit.SECONDS.sleep(5);
			}
			
			//Start transfer
	    	ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
	    	objOut.writeObject(information);
	    	objOut.flush();
			objOut.close();
				
		}
		finally{
			socket.close();	
		}
	}
}

//Function to insert into the database
public static boolean Add(String jsonString) throws UnknownHostException {
    try{
        // connect to the local database server
        MongoClient mongoClient = new MongoClient();

        // get handle to "mydb"
        DB db = mongoClient.getDB("mydb");

        // Authenticate - optional
        // boolean auth = db.authenticate("foo", "bar");

        DBCollection collection = db.getCollection("testCollection");
        try
        {
            DBObject dbObject =  (DBObject) JSON.parse(jsonString);

            int id = (Integer) dbObject.get("cid");

            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("cid", id);
            DBObject cursor = collection.findOne(searchQuery);
            if(cursor != null)
            {
                collection.update(searchQuery, dbObject);
                return true;
            }

            collection.insert(dbObject);
            return true;
        }
        catch(JSONParseException e){

        System.out.println("error");
        return false;
        }
    }
    catch(MongoException e)
    {

        System.out.println("error");
        e.printStackTrace();
        return false;
    }
}


//Function to Extract from the database

static List<String> results = new ArrayList<String>();

public static List<String> Add(String jsonString) throws UnknownHostException {
    try
    {
        // connect to the local database server
        MongoClient mongoClient = new MongoClient();

        // get handle to "mydb"
        DB db = mongoClient.getDB("mydb");

        // Authenticate - optional
        // boolean auth = db.authenticate("foo", "bar");

        DBCollection collection = db.getCollection("testCollection");

        DBCursor cursor = collection.find();


        while(cursor.hasNext()) {
            results.add(cursor.next().toString());
        }
        return results;

    }
    catch(MongoException e)
    {
        e.printStackTrace();
        return results;
    }
}
