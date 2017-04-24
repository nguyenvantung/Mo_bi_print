/**
 * 
 */
package com.plustech.print.drivers.service;

import java.io.DataOutputStream;
import java.net.Socket;

import com.plustech.print.common.PaperSizes.PaperSize_C;
import com.plustech.print.common.ServiceInfo;
import com.plustech.print.common.ServiceSupported;
import com.plustech.print.drivers.discovery.Printer;
import com.plustech.print.drivers.format.FormatPCL;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class RawPCLServiceInfo extends ServiceInfo {

	/**
	 * 
	 */
	public RawPCLServiceInfo() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param _friendlyName
	 * @param _serviceSupported
	 * @param _message
	 * @param _protocol
	 * @param _format
	 * @param _preferFormat
	 * @param _index
	 */
	public RawPCLServiceInfo(Context context, String _friendlyName,
			ServiceSupported _serviceSupported, String _message,
			String _protocol, String _format, String _preferFormat, int _index) {
		super(context, _friendlyName, _serviceSupported, _message, _protocol,
				_format, _preferFormat, _index);
		// TODO Auto-generated constructor stub
	}

	// (Printer printer, String srcString, PaperSize_C paperSize, int
	// numberOfCopies, int jobCount)
	public boolean printText(Printer printer, String srcString,
			PaperSize_C paperSize, int numberOfCopies, int jobCount) {
		super.printText(printer, srcString, paperSize, numberOfCopies, jobCount);
		// Start printing thread
		new SendText().run();
		return true;
	}

	public boolean printImage(Printer printer, Bitmap srcBitmap,
			PaperSize_C paperSize, int numberOfCopies, int jobCount) {
		super.printImage(printer, paperSize, numberOfCopies, jobCount);
		Log.i("RawPCLServiceInfo", "SendImage");
		new SendImage().run();
		return true;
	}

	protected class SendImage implements Runnable {
		@Override
		public void run() {
			try {
				Log.i("RawPCLServiceInfo", "Run.PrintImage");
				if (printer.getAddress() != "") {
					Socket socket = new Socket(printer.getAddress(), 9100);
					try {
						DataOutputStream pclData = new DataOutputStream(
								socket.getOutputStream());
						FormatPCL formatPCL = new FormatPCL(context, pclData);
						for (String element : imagePaths) {
							formatPCL.formatPCLForImage(element, paperSize,
									numberOfCopies, false);
							// break;
						}
						pclData.close();
						Log.i("SocketSend", "pclData = " + pclData);
					} catch (Exception e) {
						Log.e("SocketSend", "S: Error", e);
					}
					socket.close();
					Log.d("ClientActivity", "C: Closed.");
				}
			} catch (Exception e) {
				Log.e("ClientActivity", "C: Error", e);
			}
		}

	}

	protected class SendText implements Runnable {
		@Override
		public void run() {
			Log.e("RawPCLServiceInfo", "trying to send text");
			try {
				if (printer.getAddress() != "") {
					Socket socket = new Socket(printer.getAddress(), 9100);
					try {
						DataOutputStream pclData = new DataOutputStream(
								socket.getOutputStream());
						FormatPCL formatPCL = new FormatPCL(context, pclData);
						formatPCL.formatPCLForText(srcString, paperSize,
								numberOfCopies);
						pclData.close();
						Log.i("SocketSend", "pclData = " + pclData);
					} catch (Exception e) {
						Log.e("SocketSend", "S: Error", e);
					}
					socket.close();
					Log.d("ClientActivity", "C: Closed.");
				}
			} catch (Exception e) {
				Log.e("ClientActivity", "C: Error", e);
			}
		}
	}
}
