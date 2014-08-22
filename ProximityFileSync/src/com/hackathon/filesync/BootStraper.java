package com.hackathon.filesync;
import java.io.*;
import java.util.*;

import com.hackathon.proximity.logic.ProximityManager;
import com.hackathon.proximity.logic.User;
import com.hackathon.proximity.persistence.PersistantManager;

public class BootStraper {
	public static void start()
	{
		User user;
		ProximityManager proximityManager = ProximityManager.getInstance();
		PersistantManager persistantManager = PersistantManager.getInstance();
		 List<String> results = persistantManager.getUserInfoFromDB();
		 for(String jUserInfo : results)
		 {
		    user = CommonUtility.createUser(jUserInfo);
		    proximityManager.addUser(user);
		 }
		
	}
	
	public static void saveUsers()
	{
		ProximityManager proximityManager = ProximityManager.getInstance();
		PersistantManager persistantManager = PersistantManager.getInstance();
		//read from ProximityManager and save it through persistentManager
		//persistantManager.deleteDB();
		
	}

}
