package com.plustech.print.util.network;

import com.plustech.print.common.Common;
import com.plustech.print.common.Common.NETWORK_STATE;
import com.plustech.print.util.thread.ThreadUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {

		NETWORK_STATE status = NetworkUtil.getConnectivityStatus(context);

		// Send status to what activity register receiver with filter is
		// Common.INTERNET_RECEIVER_ACTION and key is Common.INTERNET_RECEIVER_KEY
		Common.sendInternetStatus(NetworkUtil
				.getConnectivityStatusString(context));

		if (status == NETWORK_STATE.TYPE_NOT_CONNECTED) {
			Common.sendProgressState("", true);
			ThreadUtil.stopThread(Common.THREAD_HAVE_TO_STOP);
		}

		// Toast.makeText(context, status, Toast.LENGTH_LONG).show();
	}
}
