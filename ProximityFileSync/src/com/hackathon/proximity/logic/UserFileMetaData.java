package com.hackathon.proximity.logic;
import java.io.*;

public class UserFileMetaData {
	private String m_checksum;
	private Integer m_version;
	private Integer globalNumber;
	private String m_fileName;

	public UserFileMetaData(String checksum, Integer version, String fileName) {
		super();
		this.m_checksum = checksum;
		this.m_version = version;
		this.m_fileName = fileName;
		globalNumber = 1;
	}

	public String getFileName() {
		return m_fileName;
	}

	public void setFileName(String fileName) {
		this.m_fileName = fileName;
	}

	public int getGlobalNumber() {
		return globalNumber;
	}

	public void setGlobalNumber(Integer globalNumber) {
		this.globalNumber = globalNumber;
	}

	public void incrementGlobalNumber() {
		globalNumber++;
	}

	public void decrementGlobalNumber() {
		if (0 < globalNumber) {
			globalNumber--;
		}
	}

	public Boolean isFileAvailableInProximity() {
		return globalNumber > 0;
	}

	public String getChecksum() {
		return m_checksum;
	}

	public void setChecksum(String checksum) {
		this.m_checksum = checksum;
	}

	public int getVersion() {
		return m_version;
	}

	public void setVersion(Integer version) {
		this.m_version = version;
	}

}
