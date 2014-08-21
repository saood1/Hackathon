package com.hackathon.filesync;
import java.io.*; 
public class PersistantManager {

	private static PersistantManager s_singleton = null;
	public static PersistantManager getInstance() {
		  if(s_singleton == null) {
		     synchronized(ProximityManager.class) {
		       if(s_singleton == null) {
		    	   s_singleton = new PersistantManager();
		       }
		    }
		  }
		  return s_singleton;
		}
	
	private PersistantManager()
	{
		
	}

}
/*
 * 1- bootstraper to read json and bring up proximitymanager
 * 2- refresh logic--client side
 * 3- remove user logic[done]
 * 4- update user, file and proximitymanager[done]
 * 6- add user and json string parser and save in text file
 * 5- syn and exception handling
 */
*/