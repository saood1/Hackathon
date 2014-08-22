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
	List<UserFileMetaData> m_userFileMetaDataList = new ArrayList<UserFileMetaData>();
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

	public User(String userId, Integer uid, ClientData client) {
		super();
		this.m_userId = userId;
		this.m_client = client;
		this.uid = uid;
	}

	public String getUserId() {
		return m_userId;
	}

	public List<UserFileMetaData> getUserFileMetaDataList() {
		return m_userFileMetaDataList;
	}

	public void addFileMetaData(UserFileMetaData userFileMetaData)
	{
		m_userFileMetaDataList.add(userFileMetaData);
	}

	public void setUserFileMetaDataList(
			List<UserFileMetaData> userFileMetaDataList) {
		this.m_userFileMetaDataList.addAll(userFileMetaDataList);
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
