package com.hackathon.filesync;

public class Constants {
	
	//Server Details
	public static final int SERVER_PORT_NO                       = 65535;
	public static final String SERVER_IP_ADDRESS                 = "10.204.27.20";
	
	//Strings for JSON Keys
	public static final String PORT_NO                           = "PORTNO";
	public static final String IP_ADDRESS                        = "IP_ADDRESS";
	public static final String XCORDINATES                       = "XCORDINATES";
	public static final String YCORDINATES                       = "YCORDINATES";
	public static final String USER_ID                           = "USER_ID";
	public static final String RECIPIENT_USER_ID                 = "RECIPIENT_USER_ID";
	public static final String UUID                              = "UUID";
	public static final String ONLINE                            = "ONLINE";
	
	//JSON Task keys
	public static final String SERVER_SAVE_CLIENT_INFORMATION    = "SERVER_SAVE_CLIENT_INFORMATION";
	public static final String CLIENT_DETAILS                    = "CLIENT_DETAILS";
	public static final String CLIENT_UPDATE_INFO			     = "CLIENT_UPDATE_INFO";
	public static final String CLIENT_FILE_RECEIEVE_REQUEST	     = "CLIENT_FILE_RECEIEVE_REQUEST";
	public static final String CLIENT_FILE_SEND_REQUEST		     = "CLIENT_FILE_SEND_REQUEST";
	public static final String SERVER_SHARE_REQUEST 			 = "SERVER_SHARE_REQUEST";
	
	//JSON Sender/Receiver Information keys
	public static final String FILE                              = "FILE";
	public static final String FILES                             = "FILES";
	public static final String FILE_DETAILS                      = "FILE_DETAILS";
	public static final String FILE_NAME                         = "FILE_NAME";
	public static final String FROM                              = "FROM";
	public static final String SENDER_NAME                       = "SENDER_NAME";
	public static final String SENDER_IPADDRESS                  = "SENDER_IPADDRESS";
	public static final String SENDER_PORTNO                     = "SENDER_PORTNO";
	public static final String RECEIVER_NAME                     = "RECEIVER_NAME";
	public static final String RECEIVER_IP_ADDRESS               = "RECEIVER_IP_ADDRESS";
	public static final String RECEIVER_PORTNO                   = "RECEIVER_PORTNO";
	
	//Shared dir name
	public static final String SHARED_DIR                        = System.getProperty("user.home") + "/hackathon_share/";
	
}
