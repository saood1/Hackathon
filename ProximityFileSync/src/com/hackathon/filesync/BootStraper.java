package com.hackathon.filesync;
import java.io.*;
import java.util.*;

public class BootStraper {
	public static void start()
	{
		User user;
		ProximityManager proximityManager = ProximityManager.getInstance();
		PersistantManager persistantManager = PersistantManager.getInstance();
		//read from persistentdb and add to proximityManager
		//proximityManager.addUser(user);
	}
	
	public static void saveUsers()
	{
		ProximityManager proximityManager = ProximityManager.getInstance();
		PersistantManager persistantManager = PersistantManager.getInstance();
		Map<String, User> mapFromIdToUser = proximityManager.getMapFromIdToUser();
		//read from ProximityManager and save it through persistentManager
		//persistantManager.deleteDB();
		for(User user : mapFromIdToUser.values())
		{
		 
		}
	}

}