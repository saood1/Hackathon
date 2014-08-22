package com.hackathon.filesync;

public class Constants {
	
	//Server Details
	public static final int SERVER_PORT_NO                  = 65535;
	public static final String SERVER_IP_ADDRESS            = "127.0.0.1";
	
	//Strings for JSON Keys
	public static final String PORT_NO                      = "PORTNO";
	public static final String IP_ADDRESS                   = "IP_ADDRESS";
	public static final String CORDINATES                   = "CORDINATES";
	public static final String USER_ID                      = "USER_ID";
	public static final String UUID                         = "UUID";
	
	//JSON Task keys
	public static final String CLIENT_INFORMATION           = "CLIENT_INFORMATION";
	public static final String CLIENT_DETAILS               = "CLIENT_DETAILS";
	
	public static final String TASK_INFORMATION             = "TASK_INFORMATION";
	public static final String SEND_FILE                    = "SEND_FILE";
	public static final String RECEIVE_FILE                 = "RECEIVE_FILE";
	
	public static final String FILE                         = "FILE";
	public static final String FILES                        = "FILES";
	public static final String FILE_DETAILS                 = "FILE_DETAILS";
	public static final String FILE_NAME                    = "FILE_NAME";
	public static final String FROM                         = "FROM";
	
	//Shared dir name
	public static final String SHARED_DIR                   = System.getProperty("user.home") + "/hackathon_share/";
}
