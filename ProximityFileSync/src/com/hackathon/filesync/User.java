package com.hackathon.filesync;
import java.io.*;
import java.util.*;

public class User {
	private String m_userId;
	private Client m_client;
	Map<String, UserFileMetaData> m_userFileMetaDataMap = new HashMap<String, UserFileMetaData>();
	float x, y;
    public float getX() {
		return m_client.getLocation().getLattitude();
	}

	public float getY() {
		return m_client.getLocation().getLongitude();
	}

	
	private int uid;
	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public User(String userId, Client client) {
		super();
		this.m_userId = userId;
		this.m_client = client;
	}

	public String getUserId() {
		return m_userId;
	}

	public UserFileMetaData getUserFileByChecksum(String checksum)
	{
	  return m_userFileMetaDataMap.get(checksum);
	}
	public Map<String, UserFileMetaData> getUserFileMetaDataMap() {
		return m_userFileMetaDataMap;
	}

	public void addFileMetaData(UserFileMetaData userFileMetaData)
	{
		m_userFileMetaDataMap.put(userFileMetaData.getChecksum(), userFileMetaData);
	}
	
	public void setUserFileMetaDataList(
			Map<String, UserFileMetaData> userFileMetaDataList) {
		this.m_userFileMetaDataMap.putAll(userFileMetaDataList);
	}

	public void setUserId(String userId) {
		this.m_userId = userId;
	}

	public Client getClient() {
		return m_client;
	}

	public void setClient(Client client) {
		this.m_client = client;
	}

}
