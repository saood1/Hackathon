package com.hackathon.filesync;
import java.io.*;

public class GeoLocation {
	private float m_lattitude;
	private float m_longitude;
	private String m_state;
	private String m_country;

	public GeoLocation(float lattitude, float longitude,
			String state, String country) {
		super();
		this.m_lattitude = lattitude;
		this.m_longitude = longitude;
		this.m_state = state;
		this.m_country = country;
	}

	
	public float getLattitude() {
		return m_lattitude;
	}

	public void setLattitude(float lattitude) {
		this.m_lattitude = lattitude;
	}

	public float getLongitude() {
		return m_longitude;
	}

	public void setLongitude(float longitude) {
		this.m_longitude = longitude;
	}

	public String getState() {
		return m_state;
	}

	public void setState(String state) {
		this.m_state = state;
	}

	public String getCountry() {
		return m_country;
	}

	public void setCountry(String country) {
		this.m_country = country;
	}
}
