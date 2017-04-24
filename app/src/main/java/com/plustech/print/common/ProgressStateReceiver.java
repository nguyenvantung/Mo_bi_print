package com.plustech.print.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

public class ProgressStateReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Message m = new Message();
		Bundle data = new Bundle();
		
		String status = intent.getExtras().getString(Common.PROGRESS_STATE_CHANGE_RECEIVER_KEY);
		boolean isFinished = intent.getExtras().getBoolean(Common.PROGRESS_STATE_END_RECEIVER_KEY);
		
		data.putString(Common.UPDATE_STATUS_KEY, status);
		m.setData(data);
		
		if(isFinished){
			m.what = Common.UPDATE_FINISHED;
		}else{
			m.what = Common.UPDATE_STARTED;
		}
		
		Common.getProgressDialogHandler(context).sendMessage(m);
	}
}