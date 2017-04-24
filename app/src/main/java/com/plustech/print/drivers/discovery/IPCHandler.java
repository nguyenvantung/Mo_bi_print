package com.plustech.print.drivers.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.plustech.print.drivers.netlib.Packet;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Allow the network thread to send us messages via this IPC mechanism.
 * 
 * @author NVan
 */
public class IPCHandler extends Handler {

	private static final int MSG_SET_STATUS = 1;
	private static final int MSG_ADD_PACKET = 2;
	private static final int MSG_ERROR = 3;
	String TAG = IPCHandler.class.getSimpleName();
	Packet packet;
	public List<Packet> packets = new ArrayList<Packet>();
	public ArrayList<Printer> printers;

	public IPCHandler ipc;

	public IPCHandler() {
		this.printers = new ArrayList<Printer>();
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);

		// don't process incoming IPC if we are paused.
		if (packets == null) {
			Log.w(TAG, "dropping incoming message: " + msg);
			return;
		}

		switch (msg.what) {
		case MSG_SET_STATUS:
			Log.i(TAG, "dropping incoming message: " + (String) msg.obj);
			break;
		case MSG_ADD_PACKET:
			packet = (Packet) msg.obj;
			packets.add(packet);
			Log.i(TAG, "Packet description: " + packet.description);
			Log.i(TAG,
					"Packet src getHostAddress: " + packet.src.getHostAddress());
			Log.i(TAG,
					"Packet dst getHostAddress: " + packet.dst.getHostAddress());
			
			
			
			if (!packet.description.contains("?")) {
				String ip = getIP(packet.description);
				try {
					String name = "", rp;
					String _type="";
					for (String type : PrinterDiscovery.prot) {
						_type = type.replace("local.", "local");
						if (packet.description.contains(_type)) {
							name = packet.description.replace("."+_type, "");
							name = name.replace(_type, "");
							name = name.replace(ip, "").trim();
							// name = name.replace("@", "").trim();
							name = name.replace("PTR", "").trim();
							name = name.replace("PTR", "").trim();
//							name += " at " + packet.src.getHostAddress();
							packet.type = type;
							packet.name = name;
							// InetAddress inetAddress =
							// InetAddress.getByName(ip);
							rp = "printers/_" + ip.replaceAll("\\.", "_");
							String documentType = "application/postscript";
							if (_type.contains("_ipp")) {
								documentType = "application/vnd.hp-PCL";
							}
							Log.i("", "Printer rp: " + rp);
							Printer printer = new Printer(name,
									packet.src.getHostAddress(), type, rp,
									documentType, packet.printState, "0", "0");

							Log.i(TAG, "printer ip: " + printer.getAddress());
							Log.i(TAG, "printer type: " + printer.getType());
							Log.i(TAG, "printer name: " + printer.getName());

							printers = new ArrayList<Printer>();
							
							printers.add(printer);
							Log.i(TAG,
									"Send to UI printer: " + printer.getName());
							PrinterDiscovery
									.sendPrinterStateToUI(printers);
							break;
						}
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Printer printer = new Printer("", packet.dst);
			}

			break;
		case MSG_ERROR:
			packet = new com.plustech.print.drivers.netlib.Packet();
			packet.description = ((Throwable) msg.obj).getMessage();
			packets.add(packet);
			break;
		default:
			Log.w(TAG, "unknown activity message code: " + msg);
			break;
		}
	}

	public void setStatus(String status) {
		sendMessage(Message.obtain(ipc, MSG_SET_STATUS, status));
	}

	public void addPacket(Packet packet) {
		sendMessage(Message.obtain(ipc, MSG_ADD_PACKET, packet));
	}

	public void error(Throwable throwable) {
		sendMessage(Message.obtain(ipc, MSG_ERROR, throwable));
	}

	public String getIP(String ipString) {
		String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		Matcher matcher = pattern.matcher(ipString);
		if (matcher.find()) {
			return matcher.group();
		} else {
			return "0.0.0.0";
		}
	}
}
