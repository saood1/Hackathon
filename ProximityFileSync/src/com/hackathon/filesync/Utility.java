package com.hackathon.filesync;
import java.io.*;
import java.util.*;

import gnu.trove.TIntProcedure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hackathon.proximity.logic.ClientData;
import com.hackathon.proximity.logic.GeoLocation;
import com.hackathon.proximity.logic.User;
import com.hackathon.proximity.logic.UserFileMetaData;
import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.SpatialIndex;
import com.infomatiq.jsi.rtree.RTree;

public class Utility {
	public static User createUser(ClientData client, String userId,
			List<UserFileMetaData> list) {
		User user = new User(userId, client);		
		return user;
	}

	public static Rectangle getPointRectangle(float x, float y)
	{
		Rectangle r = new Rectangle(x, y, x + 0.5f, y + 0.5f);
		return r;
	}
	
	public static GeoLocation createGeoLocation(float lattitude, float longitude,
			String state, String country)
	{

	return new GeoLocation( lattitude,  longitude,
			 state,  country);
	}
	public static ClientData createClient(GeoLocation location, String ip, Integer port) {
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
	
	/*public static void copyClientInfo(ClientData src, ClientData dest)
	{
	      dest.setIp(src.getIp());
	      dest.setLocation(src.getLocation());
	      dest.setPort(src.getPort());
	}
	
	public static void copyFileMetaDataInfo(UserFileMetaData src, UserFileMetaData dest)
	{
	      dest.setChecksum(src.getChecksum());
	      dest.setFileName(src.getFileName());
	      dest.setVersion(src.getVersion());	
	      
	}*/
	
	public static double distance(float x1, float y1, float x2, float y2)
	{
		return java.awt.geom.Point2D.distance(x1, y1, x2, y2);
	}
	
	/*public static void copyUserInfo(User src, User dest)
	{	      
		   dest.setUserId(src.getUserId());
	      
	      copyClientInfo(src.getClient(), dest.getClient());
	      for (UserFileMetaData userFileMetaDataSrc : src.getUserFileMetaDataMap().values()) {
	    	  UserFileMetaData userFileMetaDataDest = dest.getUserFileByChecksum(userFileMetaDataSrc.getChecksum());
	    	  if(null != userFileMetaDataDest)
	    	  {
	    		  copyFileMetaDataInfo(userFileMetaDataSrc, userFileMetaDataDest);
	    	  }    	 
	    	}

	}*/
}
