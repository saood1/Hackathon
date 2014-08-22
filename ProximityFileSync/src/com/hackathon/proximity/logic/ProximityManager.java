package com.hackathon.proximity.logic;
import gnu.trove.TIntProcedure;

import java.io.*; 
import java.util.*; 

import com.hackathon.filesync.Utility;
import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.SpatialIndex;
import com.infomatiq.jsi.rtree.RTree;
public class ProximityManager {
	private Map<String, User> m_mapFromIdToUser = new HashMap<String, User>();
	private Map<String, UserFileMetaData> mapFromChecksumToFile = new HashMap<String, UserFileMetaData>(); 
	private Map<Integer, String> m_mapFromIndexToUserID = new HashMap<Integer, String>();

	private Map<String, SpatialIndex> m_mapFromChecksumToProximityUsersRTree = new HashMap<String, SpatialIndex>();
	private static ProximityManager s_singleton = null;
	public static int id;
	//assumption: Client or geo location can not be same for two users and location should be auto-updated if client moves later
	
	
	public static ProximityManager getInstance() {
		  if(s_singleton == null) {
		     synchronized(ProximityManager.class) {
		       if(s_singleton == null) {
		    	   s_singleton = new ProximityManager();
		       }
		    }
		  }
		  return s_singleton;
		}
	
	private ProximityManager() {
		super();
	}
	
	
	public Map<String, User> getMapFromIdToUser() {
		return m_mapFromIdToUser;
	}
	public void setMapFromIdToUser(Map<String, User> mapFromIdToUser) {
		this.m_mapFromIdToUser = mapFromIdToUser;
	}
	
	public Boolean isFileAvailable(String checksum)
	{
		return mapFromChecksumToFile.get(checksum) != null;
		
	}
	
	public String getUserID(int index) {
		return m_mapFromIndexToUserID.get(index);
	}

	public int addUserID(String userId) {
		int index = userId.hashCode();
		m_mapFromIndexToUserID.put(index, userId);
		return index;
	}
	
	
	public void removeUserID(int index) {
		m_mapFromIndexToUserID.remove(index);
	}
	
	
	public void addUser(User user)
	{
		User userOrg = m_mapFromIdToUser.get(user.getUserId());
		if(null != userOrg)
		{
			 updateUser(user);
			 return;
		}	

		int index = addUserID(user.getUserId());
		
		
		user.setUid(index);
		m_mapFromIdToUser.put(user.getUserId(), user);
		for (UserFileMetaData fileInfo : user.getUserFileMetaDataMap().values())
		{
			UserFileMetaData dbFileInfo = mapFromChecksumToFile.get(fileInfo.getChecksum());
			if(null == dbFileInfo)
			{
				mapFromChecksumToFile.put(fileInfo.getChecksum(), fileInfo);
			}
			else
			{
				dbFileInfo.incrementGlobalNumber();
			}
			
			SpatialIndex dbNearestRTree = m_mapFromChecksumToProximityUsersRTree.get(fileInfo.getChecksum());
			
					
			if(null == dbNearestRTree)
			{				
				dbNearestRTree = new RTree();
				dbNearestRTree.init(null);
				dbNearestRTree.add(Utility.getPointRectangle(user.getX(), user.getY()), index);
				m_mapFromChecksumToProximityUsersRTree.put(fileInfo.getChecksum(), dbNearestRTree);
			}
			else
			{
				dbNearestRTree.add(Utility.getPointRectangle(user.getX(), user.getY()), index);
			}
			
		}      	
	
	}
	
	public Boolean isUserAvailable(String userID)
	{
		return m_mapFromIdToUser.get(userID) != null;
	}
	
	public Boolean updateUser(User user)
	{
		User userOrg = m_mapFromIdToUser.get(user.getUserId());
		if(null == userOrg)
		{
		  return false;
		}	
		removeUser(user.getUserId());
		addUser(user);
		return true;
	}
	
	public void removeUser(String userId)
	{
		 User user = m_mapFromIdToUser.get(userId);
		 for (UserFileMetaData fileInfo : user.getUserFileMetaDataMap().values())
		 {				
				SpatialIndex dbNearestRTree = m_mapFromChecksumToProximityUsersRTree.get(fileInfo.getChecksum());
				if(null != dbNearestRTree)
				{
					
					
					dbNearestRTree.delete(Utility.getPointRectangle(user.getX(), user.getY()), user.getUid());
										
					if(dbNearestRTree.size() == 0)
					{
						m_mapFromChecksumToProximityUsersRTree.remove(fileInfo.getChecksum());		
					}
				}				
				
		    fileInfo.decrementGlobalNumber();
		    if(fileInfo.getGlobalNumber() == 0)
		    {
		    	mapFromChecksumToFile.remove(fileInfo.getChecksum());
		    }
		 }
		 
		 removeUserID(user.getUid());
		 m_mapFromIdToUser.remove(userId);		
		 
	}
		
	
	public UserFileMetaData getFileMetaData(String  checksum)
	{
		return mapFromChecksumToFile.get(checksum);	       
	}
	
	public void clear()
	{
		m_mapFromIdToUser.clear();
		mapFromChecksumToFile.clear();
		m_mapFromChecksumToProximityUsersRTree.clear();
	}
	
	public void removeUser(User user)
	{
	
	}	
	public void refreshUsers()
	{
	  
	}
	
	public User getNearestUserToDest(String checksum, User srcUser, User destUser)
	{
		User user = srcUser;
		if(!isFileAvailable(checksum))
		{
		  return user;
		}
		
		refreshUsers();
		
		Point dstUserLocation = new Point(destUser.getX(), destUser.getY());
		SpatialIndex dbNearestRTree = m_mapFromChecksumToProximityUsersRTree.get(checksum);
		
		TIntProcedure proc =  new TIntProcedure() {
			public int  index;	
			public boolean execute(int i) {
					
					id= i;
					return true;
				}
		};
		dbNearestRTree.nearestN(dstUserLocation, proc, 1, Float.MAX_VALUE);
		
		return m_mapFromIdToUser.get(m_mapFromIndexToUserID.get(id));
			  
	}
	
}
