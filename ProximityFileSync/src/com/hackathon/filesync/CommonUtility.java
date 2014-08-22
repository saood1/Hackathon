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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hackathon.proximity.logic.ClientData;
import com.hackathon.proximity.logic.GeoLocation;
import com.hackathon.proximity.logic.User;
import com.hackathon.proximity.logic.UserFileMetaData;
import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;

//Mongo packages
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;

/**
 * This class holds all common functionality that can be used by server and
 * client.
 * 
 * @author mohamed.khan
 *
 */
public class CommonUtility {

	private int portNo;
	private String ipAddress;
	private Point cord;
	private String userID;
	private int uuid;
	private ArrayList<String> fileList;
	private static CommonUtility instance = null;
		
	/**
	 * Preventing object creation for this class
	 * @throws UnknownHostException
	 */
	private CommonUtility() throws UnknownHostException{
		portNo = getMyPortNo();
		ipAddress = getMyIPAddress();
		cord = getMyGeoCordinates();
		userID = getMyUserID();
		uuid = getMyUserID().hashCode();
		fileList = getMyFiles(Constants.SHARED_DIR);
	}
	
	
	/**
	 * Creates a static singleton instance of this class
	 * @return
	 * @throws UnknownHostException
	 */
	public static CommonUtility getInstance() throws UnknownHostException{
		if(instance==null){
			instance = new CommonUtility();
		}
		return instance;
	}
	
	public void initializeFileSync() throws UnknownHostException, IOException, InterruptedException, JSONException{
		//Create users shared directory if not already created
		createUserSharedDir(Constants.SHARED_DIR);
		
		//Create client info json string
		String jsonClientInfoString = constructJSONClientInformation();
		System.out.println("JSON String = " + jsonClientInfoString.toString());
		
		//Send the client information to the server
		sendClientInformationToServer(Constants.CLIENT_INFORMATION, jsonClientInfoString);
	}
	
	/**
	 * Create a socket connection, if the connection fails keep re-trying every
	 * 3 seconds until its connected
	 * 
	 * @param hostIp
	 * @param portNo
	 * @return
	 */
	private Socket socketConnect(String hostIp, int portNo) {
		boolean connected = false;
		Socket socket = null;

		// Check if the socket connects to the given ip and ports
		while (!connected) {
			try {
				socket = new Socket(hostIp, portNo);
				connected = true;
			} catch (Exception e) {
				connected = false;
				try {
					TimeUnit.SECONDS.sleep(3);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return socket;
	}

	/**
	 * Sends a file to a dedicated host with payload information and
	 * 
	 * @param hostIp
	 * @param portNo
	 * @param filePath
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	public void sendFile(String fromIp, String destIp, int destPortNo,
			String filePath) throws UnknownHostException, IOException,
			InterruptedException, JSONException {
		
		Socket socket = socketConnect(destIp, destPortNo);
		HashMap<String, byte[]> information = new HashMap<String, byte[]>();

		try {

			// Read the file
			File file = new File(filePath);
			long length = file.length();

			// Create JSON object out of the strings
			JSONObject jo = new JSONObject();
			jo.put(Constants.FROM, fromIp);
			jo.put(Constants.FILE_NAME, file.getName());

			JSONArray ja = new JSONArray();
			ja.put(jo);

			JSONObject mainObj = new JSONObject();
			mainObj.put(Constants.FILE_DETAILS, ja);

			// Add the task to map
			information.put(Constants.RECEIVE_FILE, mainObj.toString().getBytes());

			// Initialize the byte array for transfer
			byte[] fileBytes = new byte[(int) length];

			// Open a InputStream for reading from file Object
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			;
			bis.read(fileBytes);

			// Add the file payload to the map
			information.put(Constants.FILE, fileBytes);

			try {
				sendBytesThroughSocket(socket, information);
			} finally {
				fis.close();
				bis.close();
			}
		} finally {
			socket.close();
		}
	}

	/**
	 * Function takes a file byte array and writes it to a file
	 * 
	 * @param bytes
	 * @param fileName
	 */
	public void recieveFile(byte[] bytes, String fileName) {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		try {
			fos = new FileOutputStream("/Users/mohamed.khan/" + fileName);
			bos = new BufferedOutputStream(fos);
			bos.write(bytes);
			bos.flush();
			bos.close();
			fos.close();
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
	

	/**
	 * Starts the server socket
	 * 
	 * @param portNo
	 */
	@SuppressWarnings("unchecked")
	public void startSocket(Integer currentPort) {
		try {
			if(currentPort==null)
				currentPort = portNo;
			
			ServerSocket serverSocket = new ServerSocket(currentPort);

			try {
				while (true) {
					Socket socket = serverSocket.accept();
					System.out.println("Accepted connection : " + socket);

					ObjectInputStream objInp = new ObjectInputStream(socket.getInputStream());
					HashMap<String, byte[]> infoMap = (HashMap<String, byte[]>) objInp.readObject();

					/*
					 * Following conditional checks are written to handle
					 * various transactions
					 */
					if (infoMap.containsKey(Constants.CLIENT_INFORMATION)) {
						String s = new String(infoMap.get(Constants.CLIENT_INFORMATION));
						JSONObject jo = new JSONObject(s);
						System.out.println(jo.toString());
					} 
					else if (infoMap.containsKey(Constants.SEND_FILE)) {

					} 
					else if (infoMap.containsKey(Constants.RECEIVE_FILE)) {
						String s = new String(infoMap.get(Constants.RECEIVE_FILE));
						JSONObject jo = new JSONObject(s);
						JSONArray jarr = jo.getJSONArray(Constants.FILE_DETAILS);

						String fileName = jarr.getJSONObject(0).getString(Constants.FILE_NAME);
						String from = jarr.getJSONObject(0).getString(Constants.FROM);

						byte fileBytes[] = infoMap.get(Constants.FILE);
						recieveFile(fileBytes, fileName);
					}

					objInp.close();
				}
			} 
			catch (IOException e) {
				System.out.println("An IOException occured " + e.getMessage());
			} 
			finally {
				serverSocket.close();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Build the JSON string containing the client information when the
	 *         system boots up for the first time
	 */
	public String getJSONClientInfo() {
		String val = null;
		return val;

	}

	/**
	 * @return The ip address of the current system
	 */
	public String getMyIPAddress() throws UnknownHostException {
		String val = InetAddress.getLocalHost().getHostAddress();
		return val;
	}

	/**
	 * @return 5 digit port no which is unused by the system
	 */
	public int getMyPortNo() {
		int val = getRandomInteger(49152, 65534);
		return val;
	}

	/**
	 * @return x,y co-ordinates of the users location
	 */
	public Point getMyGeoCordinates() {
		return new Point(getRandomInteger(0, 30), getRandomInteger(0, 30));
	}

	/**
	 * @return a random integer between the low-high range
	 */
	public int getRandomInteger(int low, int high) {
		return (int) ((Math.random() * (high - low)) + low);
	}

	/**
	 * @return the current logged-in used id
	 */
	public String getMyUserID() {
		return System.getProperty("user.name");
	}

	/**
	 * Creates a shared directory under users home directory
	 * 
	 * @throws IOException
	 */
	public void createUserSharedDir(String userDirPath)
			throws IOException {
		File dir = new File(userDirPath);

		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	/**
	 * This method sends information to Server for processing
	 * 
	 * @param jsonString
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void sendClientInformationToServer(String key, String jsonString)
			throws UnknownHostException, IOException, InterruptedException {
		
		HashMap<String, byte[]> information = new HashMap<String, byte[]>();
		information.put(key, jsonString.getBytes());
		Socket socket = socketConnect(Constants.SERVER_IP_ADDRESS, Constants.SERVER_PORT_NO);

		// Start sending the byte information
		sendBytesThroughSocket(socket, information);
	}

	
	/**
	 * Scan all the files in a given dir
	 * @param dirPath
	 * @return
	 */
	public ArrayList<String> getMyFiles(String dirPath) {
		File folder = new File(dirPath);

		ArrayList<String> list = new ArrayList<String>();
		File files[] = folder.listFiles();
		
		if(files!=null && files.length>0){
			for (final File fileEntry : folder.listFiles()) {
				if (!fileEntry.isDirectory()) {
					list.add(fileEntry.getName());
				}
			}
				
		}
		
		return list;
	}
	
	
	/**
	 * Send byte information to through the connected socket
	 * 
	 * @param socket
	 * @param information
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void sendBytesThroughSocket(Socket socket, Object information)
			throws IOException, InterruptedException {

		try {
			// Start transfer
			ObjectOutputStream objOut = new ObjectOutputStream(
					socket.getOutputStream());
			objOut.writeObject(information);
			objOut.flush();
			objOut.close();
		} finally {
			socket.close();
		}
	}

	
	
	public User createUser(ClientData client, String userId,
			List<UserFileMetaData> list) {
		User user = new User(userId, client);
		return user;
	}

	public Rectangle getPointRectangle(float x, float y) {
		Rectangle r = new Rectangle(x, y, x + 0.5f, y + 0.5f);
		return r;
	}

	public GeoLocation createGeoLocation(float lattitude,
			float longitude, String state, String country) {

		return new GeoLocation(lattitude, longitude, state, country);
	}

	public ClientData createClient(GeoLocation location, String ip,
			Integer port) {
		return new ClientData(location, ip, port);
	}

	public UserFileMetaData createUserFileMetaData(String checksum,
			int version, String fileName) {
		return new UserFileMetaData(checksum, version, fileName);
	}

	public User createUser(String userId, GeoLocation location,
			String ip, Integer port, List<UserFileMetaData> list) {

		return createUser(createClient(location, ip, port), userId, list);
	}

	public void copyClientInfo(ClientData src, ClientData dest) {
		dest.setIp(src.getIp());
		dest.setLocation(src.getLocation());
		dest.setPort(src.getPort());
	}

	public void copyFileMetaDataInfo(UserFileMetaData src, UserFileMetaData dest) {
		dest.setChecksum(src.getChecksum());
		dest.setFileName(src.getFileName());
		dest.setVersion(src.getVersion());
	}

	public double distance(float x1, float y1, float x2, float y2) {
		return java.awt.geom.Point2D.distance(x1, y1, x2, y2);
	}

	public void copyUserInfo(User src, User dest) {
		dest.setUserId(src.getUserId());

		copyClientInfo(src.getClient(), dest.getClient());
		for (UserFileMetaData userFileMetaDataSrc : src
				.getUserFileMetaDataMap().values()) {
			UserFileMetaData userFileMetaDataDest = dest
					.getUserFileByChecksum(userFileMetaDataSrc.getChecksum());
			if (null != userFileMetaDataDest) {
				copyFileMetaDataInfo(userFileMetaDataSrc, userFileMetaDataDest);
			}
		}

	}
	
	/**
	 * Returns a JSON array from the list
	 * @param list
	 * @return
	 * @throws JSONException
	 */
	public String getJSONArrayStringFromArrayList(ArrayList<String> list) throws JSONException{
		JSONArray jArr = new JSONArray();
		if(list!=null && list.size()>0){
			for(String s:list){
				jArr.put(s);
			}
		}
		return jArr.toString();
	}
	
	/**
	 * Returns a JSON String from Client Information
	 * @return
	 * @throws UnknownHostException
	 * @throws JSONException
	 */
	public String constructJSONClientInformation() throws UnknownHostException, JSONException{
		
		//Create the JSON Object
		JSONObject jo = new JSONObject();
		jo.put(Constants.USER_ID, userID);
		jo.put(Constants.UUID, uuid);
		jo.put(Constants.PORT_NO, portNo);
		jo.put(Constants.IP_ADDRESS, ipAddress);
		jo.put(Constants.CORDINATES, cord.x + "|" + cord.y);
		jo.put(Constants.FILES, getJSONArrayStringFromArrayList(fileList));
		
		JSONArray ja = new JSONArray();
		ja.put(jo);

		JSONObject mainObj = new JSONObject();
		mainObj.put(Constants.CLIENT_DETAILS, ja);
		
		return mainObj.toString();
	}
	//Function to insert into the database
	/**
	 * @param jsonString
	 * @return success/failure
	 * @throws UnknownHostException
	 */
	public static boolean Add(String jsonString) throws UnknownHostException {
	  try{
	      // connect to the local database server
	      MongoClient mongoClient = new MongoClient();

	      // get handle to "mydb"
	      DB db = mongoClient.getDB("mydb");

	      // Authenticate - optional
	      // boolean auth = db.authenticate("foo", "bar");

	      DBCollection collection = db.getCollection("UserCollection");
	      try
	      {
	          DBObject dbObject =  (DBObject) JSON.parse(jsonString);

	          int id = (Integer) dbObject.get("UUID");

	          BasicDBObject searchQuery = new BasicDBObject();
	          searchQuery.put("UUID", id);
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

	/**
	 * @return List of json strings in the database
	 * @throws UnknownHostException
	 */
	public static List<String> Extract() throws UnknownHostException {
	  try
	  {
	      // connect to the local database server
	      MongoClient mongoClient = new MongoClient();

	      // get handle to "mydb"
	      DB db = mongoClient.getDB("mydb");

	      // Authenticate - optional
	      // boolean auth = db.authenticate("foo", "bar");

	      DBCollection collection = db.getCollection("UserCollection");

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
	// Function to delete a record in database
	public static boolean delete(int id) throws UnknownHostException 
	{
		try{
			// connect to the local database server
			MongoClient mongoClient = new MongoClient();

			// get handle to "mydb"
			DB db = mongoClient.getDB("mydb");

			// Authenticate - optional
			// boolean auth = db.authenticate("foo", "bar");

			DBCollection collection = db.getCollection("UserCollection");
			BasicDBObject document = new BasicDBObject();
			document.put("UUID", id);
				collection.remove(document);
				return true;
			}
			catch(JSONParseException e){

				System.out.println("error");
				return false;
			}
	}
}
