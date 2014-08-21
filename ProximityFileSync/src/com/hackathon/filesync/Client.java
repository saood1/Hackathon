package com.hackathon.filesync;
import java.io.*;

public class Client {
	private GeoLocation m_location;

	private String m_ip;
	private Integer m_port;

	public Client(GeoLocation location,  String ip, Integer port) {
		super();
		this.m_location = location;
		this.m_ip = ip;
		this.m_port = port;
	}

	public GeoLocation getLocation() {
		return m_location;
	}

	public void setLocation(GeoLocation location) {
		this.m_location = location;
	}

	public String getIp() {
		return m_ip;
	}

	public void setIp(String ip) {
		this.m_ip = ip;
	}

	public int getPort() {
		return m_port;
	}

	public void setPort(Integer port) {
		this.m_port = port;
	}

}
