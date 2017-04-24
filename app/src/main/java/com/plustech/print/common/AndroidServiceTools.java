package com.plustech.print.common;

import java.util.List;

import com.plustech.print.PrintApplication;
import com.plustech.print.service.PrintService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;

public class AndroidServiceTools {
	private static String LOG_TAG = AndroidServiceTools.class.getName();

	public static boolean isServiceRunning(String serviceClassName) {
		final ActivityManager activityManager = (ActivityManager) PrintApplication
				.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
		
		final List<RunningServiceInfo> services = activityManager
				.getRunningServices(Integer.MAX_VALUE);
		
		for (RunningServiceInfo runningServiceInfo : services) {
			Log.i(LOG_TAG, "runningServiceInfo: "+runningServiceInfo.service.getClassName());
			if (runningServiceInfo.service.getClassName().equals(
					serviceClassName)) {
				Log.i(LOG_TAG, "Print service is running...");
				return true;
			}
		}
		
		Log.i(LOG_TAG, "Print service stoped");
		return false;
	}

	public static void startPrintService(Activity activity, ServiceConnection mConnection) {
		if (!AndroidServiceTools.isServiceRunning(PrintService.class.getName())) {
			Log.i(LOG_TAG, "Start print service...");
			activity.bindService(
					new Intent(activity,PrintService.class),
					mConnection,Context.BIND_AUTO_CREATE);
		}
		
		AndroidServiceTools.isServiceRunning(PrintService.class.getName());
	}
	
	
	public static void stopService(Activity activity,ServiceConnection mConnection){
		activity.unbindService(mConnection);
	}

}
