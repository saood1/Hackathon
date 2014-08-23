package com.hackathon.proximity.logic;
import gnu.trove.TIntProcedure;

import java.io.*; 
import java.util.*; 

import com.hackathon.filesync.CommonUtility;
import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.SpatialIndex;
import com.infomatiq.jsi.rtree.RTree;
public class ProximityManager {
	private Map<Integer, User> m_mapFromIdToUser = new HashMap<Integer, User>();
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

	public Boolean isFileAvailable(String checksum) {
		return m_mapFromChecksumToProximityUsersRTree.get(checksum).size() != 0;
	}

	public void addUser(User user){
		User userOrg = m_mapFromIdToUser.get(user.getUid());
		if(null != userOrg) {
			updateUser(user);
			return;
		}	

		m_mapFromIdToUser.put(user.getUid(), user);
		for (UserFileMetaData fileInfo : user.getUserFileMetaDataList()) {
			SpatialIndex dbNearestRTree = m_mapFromChecksumToProximityUsersRTree.get(fileInfo.getChecksum());

			if(null == dbNearestRTree){				
				dbNearestRTree = new RTree();
				dbNearestRTree.init(null);
				dbNearestRTree.add(CommonUtility.getPointRectangle(user.getX(), user.getY()), user.getUid());
				m_mapFromChecksumToProximityUsersRTree.put(fileInfo.getChecksum(), dbNearestRTree);
			}
			else{
				dbNearestRTree.add(CommonUtility.getPointRectangle(user.getX(), user.getY()), user.getUid());
			}
		}      	
	}

	public Boolean isUserAvailable(String userID) {
		return m_mapFromIdToUser.get(userID) != null;
	}

	public Boolean updateUser(User user) {
		User userOrg = m_mapFromIdToUser.get(user.getUid());
		if(null == userOrg)	{
			return false;
		}	
		removeUser(user.getUid());
		addUser(user);
		return true;
	}

	public void removeUser(Integer uid){
		User user = m_mapFromIdToUser.get(uid);
		for (UserFileMetaData fileInfo : user.getUserFileMetaDataList()) {				
			SpatialIndex dbNearestRTree = m_mapFromChecksumToProximityUsersRTree.get(fileInfo.getChecksum());
			if(null != dbNearestRTree) {
				dbNearestRTree.delete(CommonUtility.getPointRectangle(user.getX(), user.getY()), user.getUid());
				if(dbNearestRTree.size() == 0) {
					m_mapFromChecksumToProximityUsersRTree.remove(fileInfo.getChecksum());		
				}
			}				
		}
		m_mapFromIdToUser.remove(uid);		
	}

	public void clear(){
		m_mapFromIdToUser.clear();
		m_mapFromChecksumToProximityUsersRTree.clear();
	}

	public User getNearestUserToDest(String checksum, Integer uid){
		User destUser = m_mapFromIdToUser.get(uid);
		return getNearestUserToDest(checksum, destUser);
	}

	public User getNearestUserToDest(String checksum, User destUser){
		if(!isFileAvailable(checksum)){
			return null;
		}

		Point dstUserLocation = new Point(destUser.getX(), destUser.getY());
		SpatialIndex dbNearestRTree = m_mapFromChecksumToProximityUsersRTree.get(checksum);

		TIntProcedure proc =  new TIntProcedure() {
			public boolean execute(int i) {
				id= i;
				return true;
			}
		};
		dbNearestRTree.nearestN(dstUserLocation, proc, 1, Float.MAX_VALUE);
		return m_mapFromIdToUser.get(id);
	}
	
	public User getUserFromIdMap(int userId){
		return m_mapFromIdToUser.get(userId);
	}
	
}
