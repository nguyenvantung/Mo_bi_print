package com.plustech.print.object;

import java.io.Serializable;


public class PrintRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7130853352437885785L;
	private String filePath;
	private String fileType;
	
	
	public PrintRequest(String filePath, String fileType) {
		super();
		this.filePath = filePath;
		this.fileType = fileType;
	}
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
