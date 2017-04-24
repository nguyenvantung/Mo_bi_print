package com.plustech.print.congdongandroid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckConnectivity {
	

	public static Boolean checkNow(Context con) {
		ConnectivityManager connectivityManager;
		NetworkInfo wifiInfo, mobileInfo;
		try {
			connectivityManager = (ConnectivityManager) con
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			wifiInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			mobileInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			if (wifiInfo.isConnected() || mobileInfo.isConnected()) {
				return true;
			}
		} catch (Exception e) {
			System.out
					.println("CheckConnectivity Exception: " + e.getMessage());
		}

		return false;
	}
}
