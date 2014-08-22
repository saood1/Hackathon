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
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.*;

import com.hackathon.proximity.logic.ClientData;
import com.hackathon.proximity.logic.GeoLocation;
import com.hackathon.proximity.logic.User;
import com.hackathon.proximity.logic.UserFileMetaData;
import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;

/**
 * This class holds all common functionality that can be used by server and
 * client.
 * 
 * @author mohamed.khan
 *
 */
public class CommonUtility {

	/**
	 * Create a socket connection, if the connection fails keep re-trying every
	 * 3 seconds until its connected
	 * 
	 * @param hostIp
	 * @param portNo
	 * @return
	 */
	private static Socket socketConnect(String hostIp, int portNo) {
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
	public static void sendFile(String fromIp, String destIp, int destPortNo,
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
			information.put(Constants.RECEIVE_FILE, mainObj.toString()
					.getBytes());

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
	public static void recieveFile(byte[] bytes, String fileName) {
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
	public static void startSocket(Integer portNo) {
		try {
			ServerSocket serverSocket = new ServerSocket(portNo);

			try {
				while (true) {
					Socket socket = serverSocket.accept();
					System.out.println("Accepted connection : " + socket);

					ObjectInputStream objInp = new ObjectInputStream(
							socket.getInputStream());
					HashMap<String, byte[]> infoMap = (HashMap<String, byte[]>) objInp
							.readObject();

					/*
					 * Following conditional checks are written to handle
					 * various transactions
					 */
					if (infoMap.containsKey(Constants.CLIENT_INFORMATION)) {

					} else if (infoMap.containsKey(Constants.SEND_FILE)) {

					} else if (infoMap.containsKey(Constants.RECEIVE_FILE)) {
						String s = new String(
								infoMap.get(Constants.RECEIVE_FILE));
						JSONObject jo = new JSONObject(s);
						JSONArray jarr = jo
								.getJSONArray(Constants.FILE_DETAILS);

						String fileName = jarr.getJSONObject(0).getString(
								Constants.FILE_NAME);
						String from = jarr.getJSONObject(0).getString(
								Constants.FROM);

						byte fileBytes[] = infoMap.get(Constants.FILE);
						CommonUtility.recieveFile(fileBytes, fileName);
					}

					objInp.close();
				}
			} catch (IOException e) {
				System.out.println("An IOException occured " + e.getMessage());
			} finally {
				serverSocket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Build the JSON string containing the client information when the
	 *         system boots up for the first time
	 */
	public static String getJSONClientInfo() {
		String val = null;
		return val;

	}

	/**
	 * @return The ip address of the current system
	 */
	public static String getMyIPAddress() throws UnknownHostException {
		String val = InetAddress.getLocalHost().getHostAddress();
		return val;
	}

	/**
	 * @return 5 digit port no which is unused by the system
	 */
	public static int getMyPortNo() {
		int val = getRandomInteger(49152, 65534);
		return val;
	}

	/**
	 * @return x,y co-ordinates of the users location
	 */
	public static Point getMyGeoCordinates() {
		return new Point(getRandomInteger(0, 30), getRandomInteger(0, 30));
	}

	/**
	 * @return a random integer between the low-high range
	 */
	public static int getRandomInteger(int low, int high) {
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
	 * 
	 * @throws IOException
	 */
	public static void createUserSharedDir(String userDirPath)
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
	public static void sendClientInformationToServer(String jsonString)
			throws UnknownHostException, IOException, InterruptedException {
		HashMap<String, byte[]> information = new HashMap<String, byte[]>();
		information.put(Constants.CLIENT_INFORMATION, jsonString.getBytes());
		Socket socket = socketConnect(Constants.SERVER_IP_ADDRESS,
				Constants.SERVER_PORT_NO);

		// Start sending the byte information
		sendBytesThroughSocket(socket, information);
	}

	/**
	 * Send byte information to through the connected socket
	 * 
	 * @param socket
	 * @param information
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void sendBytesThroughSocket(Socket socket, Object information)
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

	
	public static User createUser(ClientData client, String userId,
			List<UserFileMetaData> list) {
		User user = new User(userId, client);
		return user;
	}

	public static Rectangle getPointRectangle(float x, float y) {
		Rectangle r = new Rectangle(x, y, x + 0.5f, y + 0.5f);
		return r;
	}

	public static GeoLocation createGeoLocation(float lattitude,
			float longitude, String state, String country) {

		return new GeoLocation(lattitude, longitude, state, country);
	}

	public static ClientData createClient(GeoLocation location, String ip,
			Integer port) {
		return new ClientData(location, ip, port);
	}

	public static UserFileMetaData createUserFileMetaData(String checksum,
			int version, String fileName) {
		return new UserFileMetaData(checksum, version, fileName);
	}

	public static User createUser(String userId, GeoLocation location,
			String ip, Integer port, List<UserFileMetaData> list) {

		return createUser(createClient(location, ip, port), userId, list);
	}

	public static void copyClientInfo(ClientData src, ClientData dest) {
		dest.setIp(src.getIp());
		dest.setLocation(src.getLocation());
		dest.setPort(src.getPort());
	}

	public static void copyFileMetaDataInfo(UserFileMetaData src, UserFileMetaData dest) {
		dest.setChecksum(src.getChecksum());
		dest.setFileName(src.getFileName());
		dest.setVersion(src.getVersion());
	}

	public static double distance(float x1, float y1, float x2, float y2) {
		return java.awt.geom.Point2D.distance(x1, y1, x2, y2);
	}

	public static void copyUserInfo(User src, User dest) {
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

}
