package com.plustech.print.service;

import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;
import com.epson.eposprint.Print;
import com.plustech.print.PrintApplication;
import com.plustech.print.R;
import com.plustech.print.common.Common;
import com.plustech.print.common.Job;
import com.plustech.print.common.ServiceInfo;
import com.plustech.print.drivers.discovery.Printer;
import com.plustech.print.drivers.discovery.PrinterDiscovery;
import com.plustech.print.reader.ImageReader;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

public class PrintService extends Service {
	private static final String TAG = PrintService.class.getSimpleName();
	private ServiceInfo serviceInfo;
	private int jobCount = 1;
	private Printer defaultPrinter;
	private Job job;
	private PrinterDiscovery pd;
	
	
	@Override
	public void onDestroy() {
		pd = PrinterDiscovery.getInstance();
		pd.stop();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}


	/**
	 * Handler of incoming messages from clients.
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Common.PRINT_IMAGE:
				printImage();
				break;
			case Common.SETUP_JOB:
				Bundle data = msg.getData();
				job = Job.getJob(data);
				defaultPrinter = job.getPrinter();
				serviceInfo = Job.getServiceInfo(getApplicationContext(), job);
				break;
			case Common.DISCOVERY_PRINTER:
				pd = PrinterDiscovery.getInstance();
				pd.start();
				break;
			case Common.STOP_DISCOVERY_PRINTER:
				pd = PrinterDiscovery.getInstance();
				pd.stop();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	/**
	 * When binding to the service, we return an interface to our messenger for
	 * sending messages to the service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// Toast.makeText(getApplicationContext(), "binding",
		// Toast.LENGTH_SHORT).show();
		return mMessenger.getBinder();
	}

	private void printImage() {
		if (serviceInfo != null) {
			// Set image paths
			serviceInfo.imagePaths = job.getImagePaths();

			if (defaultPrinter == null) {
				if (job.getPrinter() == null) {
					Toast.makeText(getApplicationContext(),
							"Select printer please", Toast.LENGTH_LONG).show();
					return;
				}
				defaultPrinter = job.getPrinter();
			}

			Print epsonPrinter = new Print(getApplicationContext());
			// If is epson printer
			if (defaultPrinter.getType().equals("epson")) {
				try {
					epsonPrinter.openPrinter(Print.DEVTYPE_TCP,
							defaultPrinter.getName(), Print.TRUE, 1000);
					int pageNum = 0;
					for (String string : serviceInfo.imagePaths) {
						pageNum++;
						Common.sendProgressState(String.format(
								PrintApplication.getAppContext().getString(
										R.string.printing_status), pageNum),
								false);

						ImageReader reader = new ImageReader(
								PrintApplication.getAppContext(), string);
						Bitmap bitmap = reader.getBitmapAndFree();
						if (bitmap == null) {
							Log.i(TAG, "bitmap is null");
							Toast.makeText(getApplicationContext(),
									"Can not decode bitmap!", Toast.LENGTH_LONG)
									.show();
							return;
						}
						epsonPrint(epsonPrinter, "", 0, bitmap);
					}

					Common.sendProgressState("", true);
					Toast.makeText(getApplicationContext(), "Printed",
							Toast.LENGTH_LONG).show();

				} catch (Exception e) {
					epsonPrinter = null;
					Toast.makeText(getApplicationContext(),
							"" + e.getMessage(), Toast.LENGTH_LONG).show();
					return;
				}
				
			} else {
				// Other printer
				serviceInfo.printImage(defaultPrinter, job.getPaperSize(),
						job.getNumberOfCopies(), jobCount++);
			}
		} else {
			Log.d(TAG, "Select printer please!");
			Toast.makeText(getApplicationContext(), "Select printer please",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Print data to epson printer
	 * @param epsonPrinter
	 * @param printername
	 * @param language
	 * @param selectImage
	 * @throws EposException
	 */
	private void epsonPrint(Print epsonPrinter, String printername,
			int language, Bitmap selectImage) throws EposException {
		final int SEND_TIMEOUT = 10 * 1000;
		final int IMAGE_WIDTH_MAX = 512;
		// create builder
		Builder builder = new Builder(printername, language,
				getApplicationContext());

		// add command
		builder.addImage(selectImage, 0, 0,
				Math.min(IMAGE_WIDTH_MAX, selectImage.getWidth()),
				selectImage.getHeight(), Builder.COLOR_1, Builder.MODE_MONO,
				Builder.HALFTONE_DITHER, 1.0);

		// send builder data
		int[] status = new int[1];
		int[] battery = new int[1];
		try {

			epsonPrinter.sendData(builder, SEND_TIMEOUT, status, battery);
			// ShowMsg.showStatus(EposException.SUCCESS, status[0], battery[0],
			// this);
		} catch (EposException e) {
			Common.sendProgressState("", true);
			Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}
}
