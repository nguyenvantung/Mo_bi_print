package com.plustech.print.fileexplorer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.plustech.print.R;

public class ShareAppRequestReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		
		final Intent intent = new Intent(Intent.ACTION_SEND);
	   	intent.setType("text/plain");
    	String text = context.getString(R.string.share_msg, "https://market.android.com/details?id=com.plustech.print.fileexplorer");
    	intent.putExtra(Intent.EXTRA_TEXT, text);
    	intent.putExtra(Intent.EXTRA_SUBJECT, "FileExplorer");

    	context.startActivity(Intent.createChooser(intent, 	context.getString(R.string.share_via)));
	}

}
