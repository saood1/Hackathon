package com.hackathon.proximity.logic;

public class UserFileMetaData {
	private String m_checksum;
	private Integer m_version;
	private String m_fileName;

	public UserFileMetaData(String checksum, Integer version, String fileName) {
		super();
		this.m_checksum = checksum;
		this.m_version = version;
		this.m_fileName = fileName;
	}

	public void setFileName(String fileName) {
		this.m_fileName = fileName;
	}

	public String getFileName() {
		return m_fileName;
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
