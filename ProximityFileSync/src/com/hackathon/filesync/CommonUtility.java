package com.hackathon.filesync;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hackathon.proximity.logic.ClientData;
import com.hackathon.proximity.logic.GeoLocation;
import com.hackathon.proximity.logic.ProximityManager;
import com.hackathon.proximity.logic.User;
import com.hackathon.proximity.logic.UserFileMetaData;
import com.hackathon.proximity.persistence.PersistantManager;
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
	private Boolean state = true;
	private String userID;
	private int uuid;
	private ArrayList<String> fileList;
	private static CommonUtility instance = null;
	private ProximityManager proximityManager = null;


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
		state = false;
		proximityManager = ProximityManager.getInstance();
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


	/**
	 * Initializes the client launch
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	public void initializeFileSync() throws UnknownHostException, IOException, InterruptedException, JSONException{
		//Create users shared directory if not already created
		createUserSharedDir(Constants.SHARED_DIR);

		//Create client info json string
		setClientState(true);
		String jsonClientInfoString = constructJSONClientInformation();

		//Send the client information to the server
		sendClientInformationToServer(Constants.SERVER_SAVE_CLIENT_INFORMATION, jsonClientInfoString);
	}
	//comment
	/**
	 * update the client info 
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	public void updateClientInformation() throws UnknownHostException, IOException, InterruptedException, JSONException{
		fileList = getMyFiles(Constants.SHARED_DIR);
		String jsonClientInfoString = constructJSONClientInformation();

		//Send the client information to the server
		sendClientInformationToServer(Constants.SERVER_SAVE_CLIENT_INFORMATION, jsonClientInfoString);
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
			} 
			catch (Exception e) {
				System.out.println("An exception occured while connecting to a socket with IP =" + hostIp + " and portNo = " + portNo);
				connected = false;
				try {
					TimeUnit.SECONDS.sleep(3);
				} 
				catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		return socket;
	}


	/**
	 * Sends a file to a dedicated host with pay-load information and
	 * @param hostIp
	 * @param portNo
	 * @param filePath
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	public void executeClientFileSendRequest(String jsonString) throws UnknownHostException, IOException, InterruptedException {
		try{
			//Extract the client_file_request information
			JSONObject jo = new JSONObject(jsonString);

			JSONArray jarr = jo.getJSONArray(Constants.CLIENT_FILE_SEND_REQUEST);
			JSONObject fileSendRequest = jarr.getJSONObject(0);
			String senderName = fileSendRequest.getString(Constants.SENDER_NAME);
			String senderIP = fileSendRequest.getString(Constants.SENDER_IPADDRESS);
			String receiverIP = fileSendRequest.getString(Constants.RECEIVER_IP_ADDRESS);
			Integer receiverPortNo = fileSendRequest.getInt(Constants.RECEIVER_PORTNO);

			String filePath = Constants.SHARED_DIR + fileSendRequest.getString(Constants.FILE_NAME);

			HashMap<String, byte[]> information = new HashMap<String, byte[]>();

			// Read the file
			File file = new File(filePath);
			long length = file.length();

			// Create JSON object out of the strings
			JSONObject jsobObject = new JSONObject();
			jsobObject.put(Constants.FROM, senderIP);
			jsobObject.put(Constants.SENDER_NAME, senderName);
			jsobObject.put(Constants.FILE_NAME, file.getName());

			JSONArray ja = new JSONArray();
			ja.put(jsobObject);

			JSONObject mainObj = new JSONObject();
			mainObj.put(Constants.FILE_DETAILS, ja);

			// Add the task to map
			information.put(Constants.CLIENT_FILE_RECEIEVE_REQUEST, mainObj.toString().getBytes());

			// Initialize the byte array for transfer
			byte[] fileBytes = new byte[(int) length];

			// Open a InputStream for reading from file Object
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(fileBytes);

			// Add the file payload to the map
			information.put(Constants.FILE, fileBytes);

			try {
				Socket socket = socketConnect(receiverIP, receiverPortNo);
				sendBytesThroughSocket(socket, information);
			} 
			finally {
				fis.close();
				bis.close();
			} 
		}
		catch (JSONException e){
			e.printStackTrace();
		}
	}


	/**
	 * Function takes care of server share request
	 * @param jsonString
	 * @throws JSONException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void executeServerShareRequest(String jsonString) throws IOException, InterruptedException{
		try{
			//Hashmap for compiling the task information
			HashMap<String, byte[]> information = new HashMap<String, byte[]>();

			//Extract the server_share_request information
			JSONObject jo = new JSONObject(jsonString);
			String fileName = jo.getString(Constants.FILE_NAME);
			String fromName = jo.getString(Constants.SENDER_USER_ID);
			String receiverName = jo.getString(Constants.RECIPIENT_USER_ID);
			Integer receiverUUID = receiverName.hashCode();

			printClientMessage(fromName, "I wish to share a file '" + fileName + "' with " + receiverName + ", can you help me find the closest client who can deliver this");

			//Using the proximityManager, get the receiver details
			User receiverObj = proximityManager.getUserFromIdMap(receiverUUID);

			//Check if the proximity user is Null
			if(null == receiverObj){
				printServerMessage("I see that " + receiverName + " is offline, I cannot share a file with him at the moment, Ignoring the request for now!");
				return;
			}
			
			String receiverIPAddress = receiverObj.getClient().getIp();
			Integer receiverPortNo = receiverObj.getClient().getPort();

			//Find out who is the closes client to the receiver
			User proximityUser = proximityManager.getNearestUserToDest(fileName, receiverUUID);
			String senderName = proximityUser.getUserId();
			String senderIPAddress = proximityUser.getClient().getIp();
			Integer senderPortNo = proximityUser.getClient().getPort();

			//To receiver already has the file, so server ignores the requests
			if(proximityUser.getUid() == receiverObj.getUid()){
				printServerMessage("The client " + proximityUser.getUserId() + " already has the file '" + fileName + "' Ignoring the request for now!");
				return;
			}
			
			printServerMessage("I found " + senderName + " to be the closest client to " + receiverName + ", sending a share request to " + senderName);
			
			//Prepare the JSON string for next task
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(Constants.SENDER_NAME, senderName);
			jsonObject.put(Constants.SENDER_IPADDRESS, senderIPAddress);
			jsonObject.put(Constants.SENDER_PORTNO, senderPortNo);
			jsonObject.put(Constants.RECEIVER_NAME, receiverName);
			jsonObject.put(Constants.RECEIVER_IP_ADDRESS, receiverIPAddress);
			jsonObject.put(Constants.RECEIVER_PORTNO, receiverPortNo);
			jsonObject.put(Constants.FILE_NAME, fileName);

			JSONArray ja = new JSONArray();
			ja.put(jsonObject);

			JSONObject mainObj = new JSONObject();
			mainObj.put(Constants.CLIENT_FILE_SEND_REQUEST, ja);

			// Add the task to map
			information.put(Constants.CLIENT_FILE_SEND_REQUEST, mainObj.toString().getBytes());

			Socket socket = socketConnect(senderIPAddress, senderPortNo);
			sendBytesThroughSocket(socket, information);

			printServerMessage("Share request sent to " + senderName + " at " + Calendar.getInstance().getTime());

		}
		catch (JSONException e){
			e.printStackTrace();
		}
	}


	/**
	 * Function takes care of saving client information
	 * @param jsonString
	 * @throws JSONException
	 */
	public void executeServerSaveClientInformationRequest(String jsonString, boolean isUpdate) throws JSONException{
		//Save to DB
		//PersistantManager persistantManager = PersistantManager.getInstance();

		//Create a User object from json and add/(or update if user already exists) it to proximity manager and persistent manager if user is online
		JSONObject jo = new JSONObject(jsonString);
		User user = CommonUtility.createUser(jo.toString());
		
		try{
			if(user.isUserOnLine()){
				if(isUpdate){
					printServerMessage("I got a request to update " + user.getUserId() + " information, I am doing the needful now. Here's his updated information");
					printUserInformation(user);
				}	
				else{
					printServerMessage("I got a request to save " + user.getUserId() + " information, I am doing the needful now.");
					printUserInformation(user);
				}
				proximityManager.addUser(user);
			}
			else{
				printServerMessage("Removing user: " + user.getUserId() + " as I see he is offline");
				int userUID = user.getUid();
				proximityManager.removeUser(userUID);
			}	
		}
		catch(InterruptedException e){}
	}


	/**
	 * Function takes care of file receive request
	 * @param jsonString
	 * @param fileBytes
	 * @throws JSONException
	 */
	public void executeClientFileRecieveRequest(String jsonString, byte fileBytes[]) throws JSONException{
		try{
			JSONObject jo = new JSONObject(jsonString);
			JSONArray jarr = jo.getJSONArray(Constants.FILE_DETAILS);

			//Extract the client_file_recieve_request information
			String fileName = jarr.getJSONObject(0).getString(Constants.FILE_NAME);
			String from = jarr.getJSONObject(0).getString(Constants.SENDER_NAME);
			
			File f = new File(Constants.SHARED_DIR + fileName);
			
			printClientMessage(getMyUserID(), "Wow!!! ... I just recieved a file '" + fileName + "' from " + from);

			//Process the bytes received from sender and construct the file out of it
			recieveFile(fileBytes, fileName);
	
		}
		catch(InterruptedException e){}
	}


	/**
	 * Function takes a file byte array and writes it to a file
	 * @param bytes
	 * @param fileName
	 */
	public void recieveFile(byte[] bytes, String fileName) {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		try {
			fos = new FileOutputStream(Constants.SHARED_DIR + fileName);
			bos = new BufferedOutputStream(fos);
			bos.write(bytes);
			bos.flush();
			bos.close();
			fos.close();

			printClientMessage(getMyUserID(), "I saved the new file '" + fileName + "' under " + Constants.SHARED_DIR + " at " + Calendar.getInstance().getTime());
		} 
		catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
		catch(InterruptedException e){}
	}


	/**
	 * Starts the server socket
	 * @param portNo
	 * @throws InterruptedException 
	 */
	@SuppressWarnings("unchecked")
	public void startSocket(boolean isServer) throws InterruptedException {
		try {
			//Server has fixed port
			if(isServer)
				setServerPortNo(Constants.SERVER_PORT_NO);

			ServerSocket serverSocket = new ServerSocket(portNo);

			try {
				while (true) {
					Socket socket = serverSocket.accept();

					ObjectInputStream objInp = new ObjectInputStream(socket.getInputStream());
					HashMap<String, byte[]> infoMap = (HashMap<String, byte[]>) objInp.readObject();

					//Server receives Save/Update client request using which it parses the client information and saves in DB
					if (infoMap.containsKey(Constants.SERVER_SAVE_CLIENT_INFORMATION)) {
						String s = new String(infoMap.get(Constants.SERVER_SAVE_CLIENT_INFORMATION));
						executeServerSaveClientInformationRequest(s, false);
					}

					//Server receives a request to update information
					else if (infoMap.containsKey(Constants.CLIENT_UPDATE_INFO)) {
						String s = new String(infoMap.get(Constants.CLIENT_UPDATE_INFO));
						executeServerSaveClientInformationRequest(s, true);
					}

					//Server receives Share request using which it checks the nearest node
					else if (infoMap.containsKey(Constants.SERVER_SHARE_REQUEST)) {
						String s = new String(infoMap.get(Constants.SERVER_SHARE_REQUEST));
						executeServerShareRequest(s);
					}

					//Client receives send request using which it will send file bytes to the recipient
					else if (infoMap.containsKey(Constants.CLIENT_FILE_SEND_REQUEST)) {
						String s = new String(infoMap.get(Constants.CLIENT_FILE_SEND_REQUEST));
						executeClientFileSendRequest(s);
					}

					//Client receives file receive request using which it downloads the file bytes
					else if (infoMap.containsKey(Constants.CLIENT_FILE_RECEIEVE_REQUEST)) {
						String s = new String(infoMap.get(Constants.CLIENT_FILE_RECEIEVE_REQUEST));
						executeClientFileRecieveRequest(s, infoMap.get(Constants.FILE));

						//updating the server for client information
						updateClientInformation() ;
					}

					//Done processing of tasks, close the object stream
					objInp.close();
				}
			} 
			catch (IOException e) {
				System.out.println("An IOException occured " + e.getMessage());
			}
			catch (Exception e) {
				e.printStackTrace();
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
	 * @return Build the JSON string containing the client information when the system boots up for the first time
	 */
	public String getJSONClientInfo() {
		String val = null;
		return val;
	}


	/**
	 * @return The ip address of the current system
	 */
	public String getMyIPAddress() throws UnknownHostException {
		String val = getExternalIPAddress();
		//String val = InetAddress.getLocalHost().getHostAddress();
		return val;
	}

	/**
	 * Get the external IP address using amazon web ip service
	 * @return
	 */
	private String getExternalIPAddress() {
		try{
			URL whatismyip = new URL("http://checkip.amazonaws.com");
	        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        
			try {
			    String ip = in.readLine();
	            return ip;
	        }
	        finally {
	            if (in != null) {
	                try {
	                    in.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	
		}
		catch(Exception e){
			System.out.println("An exception occured while feetching the IP address of your machine");	
		}
		
		return "";
	}
	

	/**
	 * @return the current state of the client - offline or online
	 */
	public Boolean getClientState() {
		return state;
	}

	/**
	 * refresh files list
	 */
	public void refreshFileList()
	{
		fileList.clear();
		fileList = getMyFiles(Constants.SHARED_DIR);

		try {
			sendUpdatedClientInfoToServer();
		} 
		catch (JSONException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * displays current file list
	 */
	public void displayFileList()
	{
		Iterator<String> it = fileList.iterator();
		while(it.hasNext())
			System.out.println("\t" + it.next().toString());
	}

	/**
	 * check if file exists locally
	 * @param name
	 * @return
	 */
	public static Boolean isFileExists(String name)
	{
		File file = new File(Constants.SHARED_DIR + name); 
		return file.exists();
	}

	/**
	 * Set the current state of the client
	 * @param value
	 */
	public void setClientState(Boolean value) {
		state = value;
	}


	/**
	 * @return 5 digit port no which is unused by the system
	 */
	public int getMyPortNo() {
		int val = getRandomInteger(49153, 65534);
		return val;
	}

	/**
	 * @return 5 digit port no which is unused by the system
	 */
	public void setServerPortNo(Integer serverPortNo) {
		portNo = serverPortNo;
	}


	/**
	 * @return x,y co-ordinates of the users location
	 */
	public Point getMyGeoCordinates() {
		return new Point(0, getRandomInteger(0, 10));
	}


	/**
	 * Set new x,y co-ordinates
	 * @param updatedLoc
	 */
	public void setMyGeoCoordinates(Point updatedLoc){
		cord = updatedLoc;
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
	public void createUserSharedDir(String userDirPath)	throws IOException {
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
	public void sendClientInformationToServer(String key, String jsonString) throws UnknownHostException, IOException, InterruptedException {

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
				if (!fileEntry.isDirectory() && !fileEntry.isHidden() ) {
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
	private void sendBytesThroughSocket(Socket socket, Object information) throws IOException, InterruptedException {
		try {
			// Start transfer
			ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
			objOut.writeObject(information);
			objOut.flush();
			objOut.close();
		} 
		finally {
			//socket.close();
		}
	}


	/**
	 * @param client
	 * @param userId
	 * @param list
	 * @return
	 */
	public static User createUser(ClientData client, String userId,	Integer uid,List<UserFileMetaData> list) {
		User user = new User(userId, uid, client, list);
		return user;
	}


	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public static Rectangle getPointRectangle(float x, float y) {
		Rectangle r = new Rectangle(x, y, x + 0.5f, y + 0.5f);
		return r;
	}


	/**
	 * @param lattitude
	 * @param longitude
	 * @param state
	 * @param country
	 * @return
	 */
	public static GeoLocation createGeoLocation(float lattitude, float longitude, String state, String country) {
		return new GeoLocation(lattitude, longitude, state, country);
	}


	/**
	 * @param location
	 * @param ip
	 * @param port
	 * @return
	 */
	public static ClientData createClient(GeoLocation location, String ip, Integer port) {
		return new ClientData(location, ip, port);
	}


	/**
	 * @param checksum
	 * @param version
	 * @param fileName
	 * @return
	 */
	public static UserFileMetaData createUserFileMetaData(String checksum, int version, String fileName) {
		return new UserFileMetaData(checksum, version, fileName);
	}


	/**
	 * @param userId
	 * @param location
	 * @param ip
	 * @param port
	 * @param list
	 * @return
	 */
	public static User createUser(String userId, Integer uid, GeoLocation location, String ip, Integer port, List<UserFileMetaData> list) {
		return createUser(createClient(location, ip, port), userId, uid, list);
	}


	/**
	 * @param userId
	 * @param lattitude
	 * @param longitude
	 * @param state
	 * @param ip
	 * @param port
	 * @param fileNames
	 * @return
	 */
	public static User createUserWithFileList(String userId, Integer uid, float lattitude, float longitude, String state, String ip, Integer port, 
			List<String> fileNames) {

		List<UserFileMetaData> list = new ArrayList<UserFileMetaData>();
		for (String name : fileNames) {
			UserFileMetaData userFileMetaData = createUserFileMetaData(name, 0,	name);
			list.add(userFileMetaData);
		}

		User user = createUser(userId, uid, lattitude, longitude, state, ip, port, list);
		return user;
	}


	/**
	 * @param userId
	 * @param lattitude
	 * @param longitude
	 * @param state
	 * @param ip
	 * @param port
	 * @param list
	 * @return
	 */
	public static User createUser(String userId, Integer uid, float lattitude, float longitude, String state, String ip, Integer port, List<UserFileMetaData> list) {
		GeoLocation location = createGeoLocation(lattitude, longitude, "", "");
		User user = createUser(userId, uid, location, ip, port, list);
		return user;
	}


	/**
	 * @param jUserInfo
	 * @return
	 */
	public static User createUser(String jUserInfo) {
		String userId;
		float lattitude;
		float longitude;
		String ip;
		Integer port;
		Integer uid;

		List<String> fileNames = new ArrayList<String>();

		try {

			// parsing all user info from json and creating the user object
			JSONObject jo = new JSONObject(jUserInfo);
			JSONArray jarr = jo.getJSONArray(Constants.CLIENT_DETAILS);

			JSONObject JsonObj = jarr.getJSONObject(0);
			userId = JsonObj.getString(Constants.USER_ID);
			uid = JsonObj.getInt(Constants.UUID);
			ip = JsonObj.getString(Constants.IP_ADDRESS).toString();
			port = JsonObj.getInt(Constants.PORT_NO);

			boolean isUserOnline = JsonObj.getBoolean(Constants.ONLINE);

			lattitude = Float.parseFloat(JsonObj.get(Constants.XCORDINATES).toString());
			longitude = Float.parseFloat(JsonObj.get(Constants.YCORDINATES).toString());

			JSONArray jFileList = new JSONArray(JsonObj.get(Constants.FILES).toString());
			for(int i = 0 ; i < jFileList.length() ; i++){
				fileNames.add(jFileList.get(i).toString());
			}

			User user = createUserWithFileList(userId, uid, lattitude, longitude, "", ip, port, fileNames);
			user.setUserOnLine(isUserOnline);
			return user;
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double distance(float x1, float y1, float x2, float y2) {
		return java.awt.geom.Point2D.distance(x1, y1, x2, y2);
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
		jo.put(Constants.ONLINE, state);
		jo.put(Constants.PORT_NO, portNo);
		jo.put(Constants.IP_ADDRESS, ipAddress);
		jo.put(Constants.XCORDINATES, cord.x);
		jo.put(Constants.YCORDINATES, cord.y);
		jo.put(Constants.FILES, getJSONArrayStringFromArrayList(fileList));

		JSONArray ja = new JSONArray();
		ja.put(jo);

		JSONObject mainObj = new JSONObject();
		mainObj.put(Constants.CLIENT_DETAILS, ja);

		return mainObj.toString();
	}


	/**
	 * Function to insert into the database
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
			DBCollection collection = db.getCollection("UserCollection");

			try {

				JSONObject jo = new JSONObject(jsonString);
				JSONArray jarr = jo.getJSONArray(Constants.CLIENT_DETAILS);

				JSONObject JsonObj = jarr.getJSONObject(0);
				String juserinfo = JsonObj.toString();

				DBObject dbObject =  (DBObject) JSON.parse(juserinfo);

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
			}catch(JSONException e)
			{
				System.out.println("error");
				return false;
			}
		}
		catch(MongoException e){
			System.out.println("error");
			e.printStackTrace();
			return false;
		}
	}

	static List<String> results = new ArrayList<String>();

	/**
	 * Function to Extract from the database
	 * @return List of json strings in the database
	 * @throws UnknownHostException
	 */
	public static List<String> Extract() throws UnknownHostException {
		try	{
			// connect to the local database server
			MongoClient mongoClient = new MongoClient();

			// get handle to "mydb"
			DB db = mongoClient.getDB("mydb");

			// Authenticate - optional
			DBCollection collection = db.getCollection("UserCollection");
			DBCursor cursor = collection.find();

			while(cursor.hasNext()) {
				JSONObject jo = new JSONObject(cursor.next().toString());
				jo.remove("_id");
				JSONObject jo1 = new JSONObject();
				JSONArray ja = new JSONArray("["+jo.toString()+"]");
				jo1.put("CLIENT_DETAILS", ja);
				results.add(jo1.toString());
			}
			return results;
		}
		catch(MongoException e)	{
			e.printStackTrace();
			return results;
		}catch (JSONException e) {
			e.printStackTrace();
			return results;
		}
	}


	/**
	 * Function to delete a record in database
	 * @param id
	 * @return
	 * @throws UnknownHostException
	 */
	public static boolean delete(int id) throws UnknownHostException {
		try{
			// connect to the local database server
			MongoClient mongoClient = new MongoClient();

			// get handle to "mydb"
			DB db = mongoClient.getDB("mydb");
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


	/**
	 * @param jsonString
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void sendFileShareRequestToServer(String jsonString) throws IOException, InterruptedException {
		HashMap<String, byte[]> information = new HashMap<String, byte[]>();
		information.put(Constants.SERVER_SHARE_REQUEST, jsonString.getBytes());
		Socket socket = socketConnect(Constants.SERVER_IP_ADDRESS , Constants.SERVER_PORT_NO);
		sendBytesThroughSocket(socket , information);
	}


	/**
	 * @throws JSONException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void sendUpdatedClientInfoToServer() throws JSONException, IOException, InterruptedException {
		String jsonClientInfoString = constructJSONClientInformation();
		sendClientInformationToServer(Constants.CLIENT_UPDATE_INFO, jsonClientInfoString);
	}


	/**
	 * A function to print only server messages
	 * @param message
	 * @throws InterruptedException 
	 */
	private static void printServerMessage(String message) throws InterruptedException{
		TimeUnit.SECONDS.sleep(1);
		System.out.println();
		System.out.println("Server: " + message);
	}


	/**
	 * Format and print the user's information
	 * @param user
	 * @throws InterruptedException 
	 */
	private static void printUserInformation(User user) throws InterruptedException{
		TimeUnit.SECONDS.sleep(1);
		System.out.println("User's name            = " + user.getUserId());
		System.out.println("User's Id              = " + user.getUid());
		System.out.println("User's Geo-Coordinates = " + user.getX() + "," + user.getY());
		System.out.println("User's Ip Address      = " + user.getClient().getIp());
		System.out.println("User's Port No         = " + user.getClient().getPort());
		System.out.println("User's Current files   = " + user.getUserFileMetaDataList());
		System.out.println("User's Online status   = " + user.isUserOnLine());
		System.out.println();
		System.out.println();
	}


	/**
	 * A function to print only clients messages
	 * @param clientName
	 * @param message
	 * @throws InterruptedException 
	 */
	private static void printClientMessage(String clientName, String message) throws InterruptedException{
		TimeUnit.SECONDS.sleep(1);
		System.out.println();
		System.out.println(clientName + ": " + message);
	}
}
