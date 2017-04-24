package com.plustech.print.drivers.discovery;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;


import com.epson.eposprint.Print;
import com.epson.epsonio.DevType;
import com.epson.epsonio.EpsonIoException;
import com.epson.epsonio.Finder;
import com.epson.epsonio.IoStatus;
import com.plustech.print.PrintApplication;
import com.plustech.print.R;
import com.plustech.print.common.Common;
import com.plustech.print.drivers.netlib.NetThread;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class implements a network service listener to discover Printers over a
 * local wifi network, to aid this functionality this class implements the jmdns
 * Service listener which is an implementation of Multi-Cast DNS
 * 
 * @author NVan
 */
public class PrinterDiscovery implements ServiceListener, Runnable {
	public IPCHandler ipc;
	private final String TAG = this.getClass().getName();
	private NetThread netThread = null;
	public static String PRINTER_RECEIVER_ACTION = "com.cmc.printers.receiver.printerlist";
	public static String PRINTER_STATE_RECEIVER_ACTION = "com.cmc.printers.state.receiver.printerlist";
	public static String PRINTER_RECEIVER_KEY = "Printers";
	public static String PRINTER_RECEIVER_CHECK_KEY = "Printers";
	String pdl;
	String rp;
	String printState;
	Printer printer;
	ArrayList<HashMap<String, String>> printerList = null;
	ScheduledExecutorService scheduler;
	ScheduledFuture<?> future;
	Handler handler = new Handler();
	final static int DISCOVERY_INTERVAL = 500;
	boolean isStoped = false;

	/* Service type for printers */

	// private static String[] prot = new String[] {
	// // "All protocols",
	// "_http._tcp.local.", "_ftp._tcp.local.", "_atc._tcp.local.",
	// "_sftp._tcp.local.", "_tftp._tcp.local.", "_ssh._tcp.local.",
	// "_smb._tcp.local.", "_printer._tcp.local.", "_airport._tcp.local.",
	// "_afpovertcp._tcp.local.", "_nfs._tcp.local.",
	// "_webdav._tcp.local.", "_presence._tcp.local.",
	// "_eppc._tcp.local.", "_telnet._tcp.local", "_raop._tcp.local.",
	// "_ipp._tcp.local.", "_ipps._tcp.local.", "_service._dns-sd_udp.local.",
	// "_pdl-datastream._tcp.local.", "_riousbprint._tcp.local.",
	// "_daap._tcp.local", "_distcc._tcp.local.",
	// "_xserveraid._tcp.local.", "_net-assistant._tcp.local.",
	// "_workstation._tcp.local.", "_h323._tcp.local.",
	// "_sip._udp.local.", };

	public static String[] prot = new String[] {
			// "All protocols",
			"_ipp._tcp.local.", "_pdl-datastream._tcp.local." };

	/* List to store discovered printers */
	private ArrayList<Printer> printers;
	private Printer currentPrinter;

	/* Helper used for printer discovery */
	private JmDNS jmdns;

	/* Wifi Lock to keep radio ON as needed */
	private MulticastLock lock;

	/* Singleton Pattern shared instance methods ---------------------------- */
	private static PrinterDiscovery printerDiscovery;

	private PrinterDiscovery() {
		printers = new ArrayList<Printer>();
		ipc = new IPCHandler();
	}

	/**
	 * Singleton accessor to the instance
	 * 
	 * @param context
	 * @return instance - And instance of PrinterDiscovery
	 */
	public static PrinterDiscovery getInstance() {
		if (printerDiscovery == null)
			printerDiscovery = new PrinterDiscovery();
		return printerDiscovery;
	}

	
	/**
	 * Runnable delay discovery printers 
	 */
	private Runnable DelayDiscovery = new Runnable() {
	    public void run() {
	        stop();
	    }
	};
	
	/**
	 * Starts the service to discover printers in the local network
	 */
	public void start() {

		//Delay discovery printers
		new Handler().postDelayed(DelayDiscovery, Common.DELAY_DISCOVERY);

		new Thread(new Runnable() {

			@Override
			public void run() {
				startDiscovery();
			}
		}).start();

		netThread = new NetThread(this);
		netThread.start();

		// start find thread scheduler
		scheduler = Executors.newSingleThreadScheduledExecutor();

		// find start
		findStart();
	}

	private void startDiscovery() {
		printerList = new ArrayList<HashMap<String, String>>();

		// start find thread scheduler
		scheduler = Executors.newSingleThreadScheduledExecutor();

		WifiManager wifiManager = (WifiManager) PrintApplication
				.getAppContext().getSystemService(Context.WIFI_SERVICE);

		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int localIP = wifiInfo.getIpAddress();

		if (localIP != 0) {
			byte[] byteaddr = new byte[] { (byte) (localIP & 0xff),
					(byte) (localIP >> 8 & 0xff),
					(byte) (localIP >> 16 & 0xff),
					(byte) (localIP >> 24 & 0xff) };

			try {
				InetAddress addr = InetAddress.getByName("224.0.0.251");

				String hostname = InetAddress.getByName(addr.getHostName())
						.toString();
				Log.d(TAG, "hostname: " + hostname);
				/* Create an instance of JmDNS and bind it to network */
				jmdns = JmDNS.create(addr);

				// register some well known types
				for (int i = 0; i < prot.length; i++) {
					// System.out.println("Protocol:"+ prot[i]);
					Log.d(TAG, "addServiceListener: " + prot[i]);
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}

					// jmdns.registerServiceType(prot[i]);
					jmdns.addServiceListener(prot[i], this);

					Log.d(TAG, "Listening to Printers");
				}

				for (int i = 0; i < prot.length; i++) {
					Log.d(TAG, "Multicast query: " + prot[i]);
					netThread.submitQuery(prot[i]);
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* Stops the discovery service */
	public synchronized void stop() {
		if (jmdns != null) {
			for (Printer printer : printers) {
				jmdns.removeServiceListener(printer.getType(), this);
			}

			jmdns.unregisterAllServices();
			try {
				jmdns.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				jmdns = null;
			}
		}
		if (lock != null) {
			lock.release();
			lock = null;
		}

		if (netThread == null) {
			Log.e(TAG, "netThread should not be null!");
			// return;
		} else {
			netThread.submitQuit();
			netThread = null;
		}

		// stop find
		if (future != null) {
			future.cancel(false);
			while (!future.isDone()) {
				try {
					Thread.sleep(DISCOVERY_INTERVAL);
				} catch (Exception e) {
					break;
				}
			}
			future = null;
		}
		if (scheduler != null) {
			scheduler.shutdown();
			scheduler = null;
		}
		// stop old finder
		while (true) {
			try {
				Finder.stop();
				break;
			} catch (EpsonIoException e) {
				if (e.getStatus() != IoStatus.ERR_PROCESSING) {
					break;
				}
			}
		}

		isStoped = true;
		printerDiscovery = null;
	}

	public synchronized List<Printer> getPrinters() {
		return ipc.printers;
	}

	public synchronized void selectPrinter(Printer printer) {
		this.currentPrinter = printer;
	}

	public synchronized Printer getCurrentPrinter() {
		return currentPrinter;
	}

	public synchronized Printer getPrinterByName(String name) {
		for (Printer printer : ipc.printers) {
			if (printer.getName().equals(name))
				return printer;
		}
		return null;
	}

	/* Service Listener methods --------------------------------------------- */
	@Override
	public void serviceResolved(ServiceEvent ev) {
		Log.d(TAG, "service resolved: " + ev.getInfo().getName() + ", port: "
				+ ev.getInfo().getPort() + " address: "
				+ ev.getInfo().getInetAddress());

		pdl = ev.getInfo().getPropertyString("pdl");
		rp = ev.getInfo().getPropertyString("rp");
		
		
		//Get printer state
		printState = ev.getInfo().getPropertyString("printer-state");

		// 3: ide, 5: stoped
		if (printState.equals("3") || printState.equals("5"))
			printState = PrintApplication.getAppContext().getString(
					R.string.print_state_checking);
		else
			printState = PrintApplication.getAppContext().getString(
					R.string.print_state_off);

		
		
		String serviceProtocol = ev.getInfo().getQualifiedName()
				.substring(ev.getInfo().getName().length() + 1);
		Log.d(TAG, "serviceProtocol = "+serviceProtocol);
		
		String documentType = "application/postscript";
		if (ev.getType().contains("_ipp._tcp")) {
			
			if (!pdl.contains(documentType)) {
				documentType = "application/vnd.hp-PCL";
				Log.d(TAG, "documentType = 'application/vnd.hp-PCL'");
			}

			printer = new Printer(ev.getInfo().getName(), ev.getInfo()
					.getInetAddress().getHostAddress(), ev.getType(), ev
					.getInfo().getPropertyString("rp"), documentType,
					printState, "0", "0");

			sendPrinterToUI(printer);
		}
		
		if(ev.getType().contains("pdl-datastream")){
			documentType = "application/vnd.hp-PCL";
			printer = new Printer(ev.getInfo().getName(), ev.getInfo()
					.getInetAddress().getHostAddress(), ev.getType(), rp, documentType,
					printState, "0", "0");
			
			sendPrinterToUI(printer);
		}

	}

	@Override
	public void serviceRemoved(ServiceEvent ev) {
		Log.d(TAG, "Service removed: " + ev.getName());
	}

	@Override
	public void serviceAdded(ServiceEvent ev) {
		Log.d(TAG,
				"Service added, name: " + ev.getName() + ", type: "
						+ ev.getType());
		jmdns.requestServiceInfo(ev.getType(), ev.getName(), true);
	}

	/**
	 * Discovery Epson printer
	 */
	// find start/restart
	private void findStart() {
		if (scheduler == null) {
			return;
		}

		// stop old finder
		while (true) {
			try {
				Finder.stop();
				break;
			} catch (EpsonIoException e) {
				if (e.getStatus() != IoStatus.ERR_PROCESSING) {
					break;
				}
			}
		}

		// stop find thread
		if (future != null) {
			future.cancel(false);
			while (!future.isDone()) {
				try {
					Thread.sleep(DISCOVERY_INTERVAL);
				} catch (Exception e) {
					break;
				}
			}
			future = null;
		}

		// clear list
		printerList.clear();

		// get device type and find
		try {
			Finder.start(PrintApplication.getAppContext(), DevType.TCP,
					"255.255.255.255");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return;
		}

		// start thread
		future = scheduler.scheduleWithFixedDelay(this, 0, DISCOVERY_INTERVAL,
				TimeUnit.MILLISECONDS);
	}

	/**
	 * Get epson printers discoveried
	 */
	@Override
	public synchronized void run() {
		class UpdateListThread extends Thread {
			String[] list;

			public UpdateListThread(String[] listDevices) {
				list = listDevices;
			}

			@Override
			public void run() {
				
				if (list == null) {
					if (printerList.size() > 0) {
						printerList.clear();
					}
				} else if (list.length != printerList.size()) {
					printerList.clear();
					for (String name : list) {
						HashMap<String, String> item = new HashMap<String, String>();
						item.put("Address", name);
						Log.i(TAG, "Epson printer: " + name);
						printerList.add(item);

						// open
						try {
							Printer myPrinter = new Printer(name, "epson", "",
									"", "", "Ready", "0", "0");

							sendPrinterToUI(myPrinter);

						} catch (Exception e) {
							printer = null;
						}

					}
				}
			}
		}

		String[] deviceList = null;
		try {
			if(isStoped) return;
			deviceList = Finder.getResult();
			handler.post(new UpdateListThread(deviceList));
		} catch (Exception e) {
//			Log.e(TAG, e.getMessage(), e);
			return;
		}
	}

	/**
	 * Send to UI Printers discoveried by jmdns
	 * 
	 * @param _printers
	 */
	public static void sendPrinterToUI(Printer _printers) {
		Intent intent = new Intent();
		intent.setAction(PRINTER_RECEIVER_ACTION);
		ArrayList<Printer> _arrPrinter = new ArrayList<Printer>();
		_arrPrinter.add(_printers);
		intent.putParcelableArrayListExtra(PRINTER_RECEIVER_KEY, _arrPrinter);
		PrintApplication.getAppContext().sendBroadcast(intent);
	}

	/**
	 * Send to UI states of Printers
	 * 
	 * @param _printers
	 */
	public static void sendPrinterStateToUI(ArrayList<Printer> _printers) {
		Intent intent = new Intent();
		intent.setAction(PRINTER_STATE_RECEIVER_ACTION);
		intent.putParcelableArrayListExtra(PRINTER_RECEIVER_CHECK_KEY,
				_printers);
		PrintApplication.getAppContext().sendBroadcast(intent);
	}
}
