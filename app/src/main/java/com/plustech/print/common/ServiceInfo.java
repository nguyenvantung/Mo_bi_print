/**
 * 
 */
package com.plustech.print.common;

import java.util.List;

import com.plustech.print.common.PaperSizes.PaperSize_C;
import com.plustech.print.drivers.discovery.Printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;


public abstract class ServiceInfo {
	/**
	 * Temp variables to store printing parameters
	 */
	protected Printer printer;
	protected PaperSize_C paperSize;
	protected int numberOfCopies;
	
	protected String srcString;
	protected int jobCount;
	public String[] imagePaths;
	protected Context context;
	public String friendlyName;
	public ServiceSupported serviceSupported;	//indicate this service is supported or not
	public String message;				// to tell user why it isn't supported
	/** a string describing the 'driver' for this printer service
	 * 
	 * @note _printer._tcp -> Old-fashioned Unix LPR printing. The data transferred is often, but not necessarily, postscript
	 * @note _pdl-datastream._tcp -> Proprietary printer-specific command set, usually sent to TCP port 9100.
	 * @note _ipp._tcp -> IETF-Standard Internet Printing Protocol
	 * @comment PM treat others protocol like raw printer
	 */
	public String protocol;

	/* supported document format, service can support multi format, seperate by a comma
	 * PM support following formats:
	 * application/vnd.hp-PCL
	 * application/postscript
	 */
	public String format;
	/*
	 * the order of formats to try
	 * normally, pm will try to use PCL first, then postscript
	 * preferFormat = L"application/vnd.hp-PCL,application/postscript"
	 * only when using ipp, order will be postscript then pcl
	 * preferFormat = L"application/postscript,application/vnd.hp-PCL"
	 */
	public String preferFormat;
	/*
	 * Index of this serviceInfo in serviceInfos ArrayList
	 */
	public int index;
	/*
	 * formatIndex store the current format
	 * used in GetNextSupportedFormat method
	 */
	int formatIndex;
	
	/**
	 * 
	 */
	public ServiceInfo() {
		// TODO Auto-generated constructor stub
		friendlyName = "";
		serviceSupported = ServiceSupported.PRINTERSUPPORTED_UNKNOWN;
		message = "Ready";
		protocol = "raw";
		format = "application/vnd.hp-PCL,application/postscript";
		preferFormat = "application/vnd.hp-PCL,application/postscript";
		index = 0;
	}
	
	public boolean isPasteboard = false;
	
	public void setIspasteboard(boolean isPatseboard){
		this.isPasteboard = isPatseboard;
	}
	
	public ServiceInfo(Context context,	String _friendlyName, ServiceSupported _serviceSupported,
						String _message, String _protocol,
						String _format, String _preferFormat,
						int _index) {
		this.friendlyName = _friendlyName;
		this.serviceSupported = _serviceSupported;
		this.message = _message;
		this.protocol = _protocol;
		this.format = _format;
		this.preferFormat = _preferFormat;
		this.index = _index;
		this.context = context;
	}

	
	/*
	 * 
	 */
	
	boolean isFormatSupported(String _format) {
		if(_format == null) {
			return false;
		}
		return format.contains(_format);
	}
	/*
	 * Reset the format index
	 */
	public void ResetIndex() {
		formatIndex = 0;
	}
	/**
	 * GetNextSupportFormat, get next supported format in preferFormat;
	 */
	public String getNextSupportFormat() {
		String currentFormat = null;
		int currentFormatIndex;
		
		while(isFormatSupported(currentFormat) == false) {
			if(formatIndex >= preferFormat.length()) {
				//there isn't any prefer format left
				return null;
			}
					
			currentFormatIndex = preferFormat.indexOf(",", formatIndex);
			if(currentFormatIndex != -1) {
				//find next position of comma
				//Get next prefer format
				currentFormat = preferFormat.substring(formatIndex, currentFormatIndex);
				formatIndex = currentFormatIndex + 1;
			}
			else {
				//there isn't any comma in preferFormat String
				if(formatIndex < preferFormat.length()) {
					//Get last prefer format
					currentFormat = preferFormat.substring(formatIndex);
					formatIndex = preferFormat.length();
				}
			}
		}
		//return next supported format
		return currentFormat;
	}
	

	
	public boolean printText(Printer printer, String srcString, PaperSize_C paperSize, int numberOfCopies, int jobCount) {
		this.printer = printer;
		this.srcString = srcString;
		this.paperSize = paperSize;
		Log.i("ServiceInfo PrintText - setNumberofcopies", ""+ numberOfCopies);
		this.numberOfCopies = numberOfCopies;
		this.jobCount = jobCount;
		return true;
	}
		
	
	public boolean printImage(Printer printer, PaperSize_C paperSize, int numberOfCopies, int jobCount) {
		this.printer = printer;
		this.paperSize = paperSize;
		Log.i("ServiceInfo rintImage - setNumberofcopies", ""+ numberOfCopies);
		this.numberOfCopies = numberOfCopies;
		this.jobCount = jobCount;
		return true;
	}
	
	/**
	 * threads that send data to printer, must be override in ServiceInfos
	 */
	protected abstract class sendText implements Runnable {
		public void run() {
			
		}
	}
	
	protected abstract class sendImage implements Runnable {
		 public void run() {
			 
		 }
	}
	
}
