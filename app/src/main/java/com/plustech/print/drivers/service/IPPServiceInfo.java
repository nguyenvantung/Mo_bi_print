/**
 * 
 */
package com.plustech.print.drivers.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.plustech.print.common.PaperSizes.PaperSize_C;
import com.plustech.print.common.ServiceInfo;
import com.plustech.print.common.ServiceSupported;
import com.plustech.print.drivers.discovery.Printer;
import com.plustech.print.drivers.format.FormatIPP;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;
import android.util.Log;

public class IPPServiceInfo extends ServiceInfo {

	public static final String TAG = null;

	/**
	 * 
	 */
	public IPPServiceInfo() {
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
	public IPPServiceInfo(Context context, String _friendlyName,
			ServiceSupported _serviceSupported, String _message,
			String _protocol, String _format, String _preferFormat, int _index) {
		super(context, _friendlyName, _serviceSupported, _message, _protocol, _format,
				_preferFormat, _index);
		// TODO Auto-generated constructor stub
	}

	public boolean printText(Printer printer, String srcString,
			PaperSize_C paperSize, int numberOfCopies, int jobCount) {
		super.printText(printer, srcString, paperSize, numberOfCopies, jobCount);
		// Start printing thread
		Thread cThread = new Thread(new SendText());
		cThread.start();
		return true;
	}

	public boolean printImage(Printer printer,PaperSize_C paperSize, int numberOfCopies, int jobCount) {
		super.printImage(printer, paperSize, numberOfCopies,jobCount);
		// Start printing thread
		Log.i("IPPServiceInfo", "Create thread for send image");
		Thread cThread = new Thread(new SendImage());
		cThread.setName("IPPServiceInfo-PrintImage");
		cThread.start();
		return true;
	}

	private String getFirstSupportedFormat(String docFormat) {
		if (docFormat != null) {
			String[] formats = docFormat.split(",");
			for (String format : formats) {
				if ("application/postscript".equals(format) || "application/vnd.hp-PCL".equals(format)) {
					return format;
				}
			}
		}
		return null;
	}

	protected class SendImage implements Runnable {
		@Override
		public void run() {
			Log.i("IPPServiceInfo", "Start print...");
			try {
				String host = printer.getAddress();
				String post = "http://" + printer.getAddress() + ":631";
				Log.i("IPPServiceInfo",
						" - document_format = " + printer.getDocumentFormat()
								+ " - rp = " + printer.getRp());
				
				String subHost = "/" + printer.getRp();

				String printerUri = "ipp://" + host + ":631";

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				FormatIPP formatIPP = new FormatIPP(context, byteArrayOutputStream,
						jobCount, printerUri + subHost);
				// Set the document-format
				if (("application/postscript")
						.equals(getFirstSupportedFormat(printer.getDocumentFormat()))) {
					formatIPP.setDocumentFormat("application/postscript");
					post = post + subHost;
				} else
					formatIPP.setDocumentFormat("application/vnd.hp-PCL");

				formatIPP.setDocumentFormat(printer.getDocumentFormat());

				Log.i("IPPServiceInfo", "SendImage - jobCount: " + jobCount);

				if (formatIPP.formatIPPForImage(paperSize, numberOfCopies,
						imagePaths)) {
					// Create InputStreamEntity and copy ipp data from
					// inputStream
					byte[] byteArray = byteArrayOutputStream.toByteArray();
					ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
							byteArray);
					InputStreamEntity inputStreamEntity = new InputStreamEntity(
							byteArrayInputStream, byteArray.length);

					// Create HttpClient
					HttpClient httpclient = new DefaultHttpClient();
					HttpHost httpHost = new HttpHost(host, 631);

					// POST request
					HttpPost httppost = new HttpPost(post);
					// Set headers
					httppost.setHeader("User-Agent",
							"PlusTech%20Print/2.3.2.711 CFNetwork/548.0.4 Darwin/11.0.0");
					httppost.setHeader("Content-Type", "application/ipp");
					httppost.setHeader("Connection", "close");

					try {
						// Add data
						httppost.setEntity(inputStreamEntity);
						// Execute HTTP Post Request
						HttpResponse response = httpclient.execute(httpHost,
								httppost);

						// Log.i("IPPServiceInfo", "SendImage - response " +
						// inputStreamToString(response.getEntity().getContent()));
						// TODO: handle response
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						Log.e(TAG,
								"SendImage - exception: " + e.getMessage(), e);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Log.e(TAG,
								"SendImage - ioexception: " + e.getMessage(), e);
					} finally {
						// httpclient.close();
					}
				} else {
					Log.e(TAG, "formatIPP failed");
				}
			} catch (Exception e) {
				Log.e("IPPServiceInfo", "C: Error", e);
			}
		}
	}

	protected class SendText implements Runnable {
		public void run() {
			try {
				String host = printer.getAddress();
				String post = "http://" + host + ":631";
				String subHost = "/" + printer.getRp();
				// URI uri = new URI("http", null, host, 631, null, null, null);
				String printerUri = "ipp://" + host + ":631";

				if (host != "") {
					/*
					 * PipedInputStream inputStream = new PipedInputStream();
					 * PipedOutputStream outputStream = new
					 * PipedOutputStream(inputStream);
					 */
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					FormatIPP formatIPP = new FormatIPP(context, byteArrayOutputStream,
							jobCount, printerUri + subHost);
					// Set the document-format
					if (("application/postscript")
							.equals(getFirstSupportedFormat(printer.getDocumentFormat())))
						formatIPP.setDocumentFormat("application/postscript");
					else
						formatIPP.setDocumentFormat("application/vnd.hp-PCL");
					Log.i("IPPServiceInfo", "SendText - jobCount: " + jobCount);
					if (formatIPP.formatIPPForText(srcString, paperSize,
							numberOfCopies)) {
						// Create InputStreamEntity and copy ipp data from
						// inputStream
						byte[] byteArray = byteArrayOutputStream.toByteArray();
						ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
								byteArray);
						InputStreamEntity inputStreamEntity = new InputStreamEntity(
								byteArrayInputStream, byteArray.length);

						// Create HttpClient
						// HttpClient httpclient = new DefaultHttpClient();
						AndroidHttpClient httpclient = AndroidHttpClient
								.newInstance("PlustechPrintApp");

						// url:631
						HttpHost httpHost = new HttpHost(host, 631);
						// POST request
						HttpPost httppost = new HttpPost(post);
						Log.w(TAG, "host: " + host + " post: "
								+ post);
						// Set headers
						httppost.setHeader("Content-type", "application/ipp");
						httppost.setHeader("Connection", "close");
						try {
							httppost.setEntity(inputStreamEntity);
							HttpResponse response = httpclient.execute(
									httpHost, httppost);
						} catch (ClientProtocolException e) {
							Log.e(TAG, "ClientProtocolException: "
									+ e.getMessage(), e);
						} catch (IOException e) {
							Log.e(TAG,
									"IOException: " + e.getMessage(), e);
						} finally {
							httpclient.close();
						}
					} else {
						Log.e(TAG, "formatIPP failed");
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "C: Error", e);
			}
		}
	}

}
