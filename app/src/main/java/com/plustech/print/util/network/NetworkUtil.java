package com.plustech.print.util.network;

import com.plustech.print.common.Common.NETWORK_STATE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
	
//	public static int TYPE_WIFI = 1;
//	public static int TYPE_MOBILE = 2;
//	public static int TYPE_NOT_CONNECTED = 0;
	
	
	public static NETWORK_STATE getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return NETWORK_STATE.TYPE_WIFI;
			
			if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return NETWORK_STATE.TYPE_MOBILE;
		} 
		return NETWORK_STATE.TYPE_NOT_CONNECTED;
	}
	
	public static String getConnectivityStatusString(Context context) {
		NETWORK_STATE conn = NetworkUtil.getConnectivityStatus(context);
		String status = null;
		if (conn == NETWORK_STATE.TYPE_WIFI) {
			status = "Wifi enabled";
		} else if (conn == NETWORK_STATE.TYPE_MOBILE) {
			status = "Mobile data enabled";
		} else if (conn == NETWORK_STATE.TYPE_NOT_CONNECTED) {
			status = "Not connected to Internet";
		}
		return status;
	}
}