package com.hackathon.proximity.persistence;

import java.io.*; 
import java.net.UnknownHostException;
import java.util.*;

import com.hackathon.filesync.CommonUtility;
import com.hackathon.proximity.logic.ProximityManager;

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

	private PersistantManager(){}

	public List<String> getUserInfoFromDB()
	{
		List<String> results = null;
		try {
			results = CommonUtility.Extract();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return results;
	}

	public boolean addUser(String jUserInfo)
	{
		try {
			return CommonUtility.Add(jUserInfo);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}


