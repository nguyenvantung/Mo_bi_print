package com.plustech.print.common;

import android.content.Context;
import android.os.Bundle;

import com.plustech.print.common.PaperSizes.PaperSize_C;
import com.plustech.print.drivers.discovery.Printer;
import com.plustech.print.drivers.service.IPPServiceInfo;
import com.plustech.print.drivers.service.RawPCLServiceInfo;

public class Job {
	private Printer printer;
	public Printer getPrinter() {
		return printer;
	}
	public void setPrinter(Printer printer) {
		this.printer = printer;
	}
	public PaperSize_C getPaperSize() {
		return paperSize;
	}
	public void setPaperSize(PaperSize_C paperSize) {
		this.paperSize = paperSize;
	}
	public int getNumberOfCopies() {
		return numberOfCopies;
	}
	public void setNumberOfCopies(int numberOfCopies) {
		this.numberOfCopies = numberOfCopies;
	}
	private PaperSize_C paperSize;
	private int numberOfCopies;
	private String[] imagePaths;
	
	public String[] getImagePaths() {
		return imagePaths;
	}
	public void setImagePaths(String[] imagePaths) {
		this.imagePaths = imagePaths;
	}
	public Job(){}
	public Job(Printer printer, PaperSize_C paperSize, int numberOfCopies, String[] imagePaths){
		this.printer = printer;
		this.paperSize = paperSize;
		this.numberOfCopies = numberOfCopies;
		this.imagePaths = imagePaths;
	}
	
	public static Job getJob(Bundle data){
		String[] paths = data.getStringArray(Common.PATHS);
		String name = data.getString(Common.PRINTER_NAME);
		String address = data.getString(Common.PRINTER_ADDRESS);
		String documentFormat = data.getString(Common.PRINTER_FORMAT);
		String rp = data.getString(Common.PRINTER_RP);
		String type = data.getString(Common.PRINTER_TYPE);
		String papgerName = data.getString(Common.PRINTER_PAPER_NAME);
		int numberOfCopies = data.getInt(Common.PRINTER_NUMCOPY);
		String printerState = data.getString(Common.PRINTER_STATE);
		
		Printer printer = new Printer(name, address, type, rp, documentFormat, printerState, "0", "0");
		Job job = new Job();
		job.setImagePaths(paths);
		job.setPrinter(printer);
		job.setNumberOfCopies(numberOfCopies);
		PaperSizes paperSizes = new PaperSizes();
		for(PaperSize_C paperSize : paperSizes.sizesList){
			if(paperSize.name.toLowerCase().equals(papgerName.toLowerCase())){
				job.setPaperSize(paperSize);
				break;
			}
		}
		return job;
	}
	
	public static ServiceInfo getServiceInfo(Context context, Job job){
		ServiceInfo serviceInfo = new IPPServiceInfo(context, 
				"Internet Printing Protocol",
				ServiceSupported.PRINTERSUPPORTED_YES, "Ready",
				"raw", job.getPrinter().getDocumentFormat(),
				job.getPrinter().getDocumentFormat(), 0);
		if(job.getPrinter().getType().contains("pdl-datastream")){
			serviceInfo = new RawPCLServiceInfo(context, 
					"PDL datastream",
					ServiceSupported.PRINTERSUPPORTED_YES, "Ready",
					"raw", job.getPrinter().getDocumentFormat(),
					job.getPrinter().getDocumentFormat(), 0);
		}
		return serviceInfo;
	}
	
}
