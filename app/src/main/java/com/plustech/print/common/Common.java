package com.plustech.print.common;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.plustech.print.PrintApplication;
import com.plustech.print.congdongandroid.PrintPreviewImage;
import com.plustech.print.object.PrintRequest;

@SuppressLint("DefaultLocale")
public class Common {
	private static final String TAG = Common.class.getSimpleName();
	
	/** Discover delay **/
	public static final int DELAY_DISCOVERY = 300000;

	/** Thread have to stop when internet disconnected */
	public static final String[] THREAD_HAVE_TO_STOP = new String[] { "JmDNS",
			"SocketListener", "mDNS" };

	/** INTERNET */
	public static final String INTERNET_RECEIVER_ACTION = "Internet_changed_status";
	public static final String INTERNET_RECEIVER_KEY = "Status";

	public enum NETWORK_STATE {

		TYPE_NOT_CONNECTED(0), TYPE_WIFI(0), TYPE_MOBILE(1);

		public int index;

		NETWORK_STATE(int index) {
			this.index = index;
		}
	};
	
	public enum  FILE_TEST {
		 Test, Test1;
	}

	public enum FILE_TYPE {
		TYPE_PDF, TYPE_DOC, TYPE_DOCX, TYPE_XLS, TYPE_XLSX, TYPE_TXT, TYPE_IMAGE, TYPE_NONE;
	};

	/** Progress */
	public static final String PROGRESS_STATE_CHANGE_RECEIVER_ACTION = "Progress_State_changed_status";
	public static final String PROGRESS_STATE_CHANGE_RECEIVER_KEY = "Status";
	public static final String PROGRESS_STATE_END_RECEIVER_KEY = "End";
	public static final int UPDATE_STARTED = 0;
	public static final int UPDATE_FINISHED = 1;
	public static final String UPDATE_STATUS_KEY = "Status";
	private static ProgressDialog progressDialog;

	public static final String INTENT_EXTRA_RESULT = "result";

	/**
	 * How to: 1) Call this function at a activity (Example: A) for get Handler
	 * message. 2) Register receiver at a activity (Example: A)
	 * ProgressStateReceiver mPrintStateReceiver = new ProgressStateReceiver();
	 * IntentFilter printStateIntentFilter = new IntentFilter();
	 * printStateIntentFilter
	 * .addAction(Common.PROGRESS_STATE_CHANGE_RECEIVER_ACTION);
	 * registerReceiver(mPrintStateReceiver, printStateIntentFilter); 2) Call
	 * sendProgressState at any class
	 * 
	 * @param context
	 *            : Just activity (Do not use Application Context because it
	 *            haven't UI
	 * @return
	 */
	public static Handler getProgressDialogHandler(final Context context) {
		Handler dialogHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Common.UPDATE_STARTED:
					Bundle data = msg.getData();
					String status = data.getString(UPDATE_STATUS_KEY);
					progressDialog = ProgressDialog.show(context, "", status,
							true, false);
					break;
				case Common.UPDATE_FINISHED:
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
					break;
				}
			}
		};
		return dialogHandler;
	}

	/**
	 * Send state for progress
	 * 
	 * @param status
	 *            : The text show at progress
	 * @param isEnd
	 *            : true for dismiss progress
	 */
	public static void sendProgressState(String status, boolean isEnd) {
		Intent intent = new Intent();
		intent.setAction(PROGRESS_STATE_CHANGE_RECEIVER_ACTION);
		intent.putExtra(PROGRESS_STATE_CHANGE_RECEIVER_KEY, status);
		intent.putExtra(PROGRESS_STATE_END_RECEIVER_KEY, isEnd);
		PrintApplication.getAppContext().sendBroadcast(intent);
	}

	public static final int[] PCL_RESOLUTIONS = new int[] { 75, 100, 150, 300,
			600 };
	public static final int TOP2BOT = 1;
	public static final int LEFT2RIGHT = 2;
	public static final int PCL_PORTRAIT = 0;
	public static final int PCL_LANDSCAPE = 1;

	public static final int PCL_DPI_150 = 2;
	public static final int PCL_DPI_300 = 3;
	public static final int PCL_DPI_600 = 4;

	/** Paper name */
	public static final String PAPER_LETTER = "Letter";
	public static final String PAPER_A4 = "A4";
	public static final String PAPER_LEGAL = "Legal";
	public static final String PAPER_A3 = "A3";
	public static final String PAPER_LARGE = "Large Photo";
	public static final String PAPER_MEDIUM = "Medium Photo";
	public static final String PAPER_SMALL = "Small Photo";

	/** Command to the service to print */
	public static final int PRINT_IMAGE = 1;
	/** Command to the service to set image paths */
	public static final int SETUP_JOB = 2;
	/** Command to the service to discover */
	public static final int DISCOVERY_PRINTER = 3;
	/** Command to the service to stop discover */
	public static final int STOP_DISCOVERY_PRINTER = 4;

	/** Bound Key: Image paths */
	public static final String IMAGE_PATHS = "PATHS";
	/** Bound Key: Image paths */
	public static final String PATHS = "PATHS";
	/** Bound Key: printer name */
	public static final String PRINTER_NAME = "name";
	public static final String PRINTER_ADDRESS = "address";
	public static final String PRINTER_TYPE = "type";
	public static final String PRINTER_RP = "rp";
	public static final String PRINTER_FORMAT = "documentFormat";
	public static final String PRINTER_NUMCOPY = "numberOfCopies";
	public static final String PRINTER_PAPER_NAME = "paper_name";
	public static final String PRINTER_STATE = "printer_state";

	/**
	 * Send status to what activity register receiver with filter is
	 * Common.INTERNET_RECEIVER_ACTION and key is Common.INTERNET_RECEIVER_KEY
	 * This function already implement at NetworkChangeReceiver for receive
	 * status from Android network service
	 * 
	 * @param status
	 */
	public static void sendInternetStatus(String status) {
		Intent intent = new Intent();
		intent.setAction(INTERNET_RECEIVER_ACTION);
		intent.putExtra(INTERNET_RECEIVER_KEY, status);
		PrintApplication.getAppContext().sendBroadcast(intent);
	}

	public static void startPrintPreview(Context context, PrintRequest pr) {
		Intent it = new Intent(context,	PrintPreviewImage.class);
		it.putExtra("data", pr);
		context.startActivity(it);
	}

	public static Intent buildIntent(String filePath) {
		
		Log.d("filepath", ""+ filePath);
		PrintRequest pr = new PrintRequest(filePath, getFileType(filePath));
		Intent result = new Intent();
		result.putExtra(INTENT_EXTRA_RESULT, pr);
		return result;
	}

	@SuppressLint("DefaultLocale")
	public static String getFileType(String filePath) {

		if (TextUtils.isEmpty(filePath))
			return FILE_TYPE.TYPE_NONE.name();
		String filetype = null;
		if (filePath.toLowerCase().contains("pdf")) {
			filetype = FILE_TYPE.TYPE_PDF.name();

		} else if (filePath.toLowerCase().contains("doc")) {
			filetype = FILE_TYPE.TYPE_DOC.name();

		} else if (filePath.toLowerCase().contains("docx")) {

			filetype = FILE_TYPE.TYPE_DOCX.name();

		} else if (filePath.toLowerCase().contains("xls")) {

			filetype = FILE_TYPE.TYPE_XLS.name();

		} else if (filePath.toLowerCase().contains("xlsx")) {

			filetype = FILE_TYPE.TYPE_XLSX.name();

		} else if (filePath.toLowerCase().contains("txt")) {

			filetype = FILE_TYPE.TYPE_TXT.name();

		} else if (filePath.toLowerCase().contains("png")
				|| filePath.toLowerCase().contains("jpg")
				|| filePath.toLowerCase().contains("tif")) {
			Log.d(TAG, "File type is IMAGE");
			filetype = FILE_TYPE.TYPE_IMAGE.name();
		}else {
			return FILE_TYPE.TYPE_NONE.name();
		}

		return filetype;
	}

	
}
