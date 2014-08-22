package com.hackathon.proximity.logic;
import java.io.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hackathon.filesync.Constants;

public class User {
	private String m_userId;
	private ClientData m_client;
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

	public User(String userId, ClientData client) {
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

	public ClientData getClient() {
		return m_client;
	}

	public void setClient(ClientData client) {
		this.m_client = client;
	}
	
}
