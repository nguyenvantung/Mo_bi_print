package com.plustech.print;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.plustech.print.common.AndroidServiceTools;
import com.plustech.print.common.Common;
import com.plustech.print.common.ProgressStateReceiver;
import com.plustech.print.congdongandroid.PrintSelectPrinter;
import com.plustech.print.congdongandroid.PrintSeting;
import com.plustech.print.congdongandroid.WebActivity;
import com.plustech.print.drivers.discovery.Printer;
import com.plustech.print.drivers.discovery.PrinterDiscovery;
import com.plustech.print.fileexplorer.FileExplorerMain;
import com.plustech.print.object.PrintRequest;
import com.plustech.print.storage.PrinterProviderMgMt;
import com.plustech.print.storage.PrinterTable.PrinterTables;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class BaseActivity extends Activity implements OnClickListener {
	
	private static final String tag = BaseActivity.class.getSimpleName();
	PrinterDiscovery pd;
	CountDownTimer waitTimer;
	boolean active = true;
	
	public ArrayList<Printer> arrayPrinter;
	ArrayList<Printer> arrayOrgPrinters;
	ArrayList<Printer> _arrayStatePrinters;
	/** Messenger for communicating with the service. */
	public static Messenger mService = null;
	/* Receiver for get printer discovered */
	protected ListReceiver mListReceiver;
	protected IntentFilter intentFilter;
	/* Receiver for progress changed on printing */
	protected ProgressStateReceiver mPrintStateReceiver;
	protected IntentFilter printStateIntentFilter;

	/** Flag indicating whether we have called bind on the service. */
	boolean mBound;

	public static final int REQUEST_DOCUMENT = 1;
	public static final int REQUEST_PHOTO = 2;
	public static final int REQUEST_EMAIL = 3;
	public static final int REQUEST_WEBPAGE = 4;
	public static final int REQUEST_SETTING = 5;
	
	public static final int PRINT_SELECT_PRINTER = 1;  // at the PrinterSelectPrinter screen
	public static final int OTHER_PRINT_SELECT_PRINTER = 2;
	
	// setting printer
	
	public  final String FONTSIZE = "FontSize";
	public  final String SCALE = "Scale";
	public  final String MARGIN = "Margin";
	public  final String ORIENSTATION = "Orienstation";
	public  final String SIZE = "size";

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

	}

	@Override
	protected void onDestroy() {
		// Do not comment : begin
		this.unregisterReceiver(mListReceiver);
		this.unregisterReceiver(mPrintStateReceiver);
		// Do not comment : end
		stopDiscoverPrinter();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Do not comment : begin
		mListReceiver = new ListReceiver();
		intentFilter = new IntentFilter();
		intentFilter.addAction(PrinterDiscovery.PRINTER_RECEIVER_ACTION);
		intentFilter.addAction(PrinterDiscovery.PRINTER_STATE_RECEIVER_ACTION);
		registerReceiver(mListReceiver, intentFilter);

		mPrintStateReceiver = new ProgressStateReceiver();
		printStateIntentFilter = new IntentFilter();
		printStateIntentFilter.addAction(Common.PROGRESS_STATE_CHANGE_RECEIVER_ACTION);
		registerReceiver(mPrintStateReceiver, printStateIntentFilter);
		// Do not comment : end
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Start Print service
		AndroidServiceTools.startPrintService(this, mConnection);
	}


	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service. We are communicating with the
			// service using a Messenger, so here we get a client-side
			// representation of that from the raw IBinder object.
			mService = new Messenger(service);
			mBound = true;
			discoverPrinter();
		}

		
		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;
			mBound = false;
		}
	};

	public void setUpJob(List<String> imgPaths, Printer printer) {
		if (!mBound)
			return;

		if (printer == null)
			return;

		// Create and send a message to the service, using a supported 'what'
		// value
		Message msg = Message.obtain(null, Common.SETUP_JOB, 0, 0);
		Bundle data = new Bundle();
		String[] imgs = new String[imgPaths.size()];
		data.putStringArray(Common.PATHS, imgPaths.toArray(imgs));
		data.putString(Common.PRINTER_NAME, printer.getName());
		data.putString(Common.PRINTER_ADDRESS, printer.getAddress());
		data.putString(Common.PRINTER_FORMAT, printer.getDocumentFormat());
		data.putString(Common.PRINTER_RP, printer.getRp());
		data.putString(Common.PRINTER_TYPE, printer.getType());
		data.putInt(Common.PRINTER_NUMCOPY, 1);
		data.putString(Common.PRINTER_PAPER_NAME, Common.PAPER_A4);
		msg.setData(data);

		try {
			mService.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	protected void discoverPrinter() {
		Message msg = Message.obtain(null, Common.DISCOVERY_PRINTER, 0, 0);
		try {
			mService.send(msg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void stopDiscoverPrinter() {
		Message msg = Message.obtain(null, Common.STOP_DISCOVERY_PRINTER, 0, 0);
		try {
			if(mService != null)
				mService.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void print(Printer printer, List<String> imgPaths) {
		if (!mBound)
			return;

		setUpJob(imgPaths, printer);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Create and send a message to the service, using a supported 'what'
		// value
		Message msg = Message.obtain(null, Common.PRINT_IMAGE, 0, 0);

		try {
			mService.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// check os and set layout
		
		setContentView(R.layout.home);
		buttonView();
//		LoadPrinterDefault();
		
	}

	public void LoadPrinter() {
		// TODO: Set printers to listview
		String sPrinters = "";
		arrayPrinter = PrinterProviderMgMt.selectAll();		
		
		for (Printer printer : arrayPrinter) {
			Log.d(tag, "=============");
			Log.d(tag, "Address: " + printer.getAddress());
			Log.d(tag, "DocumentFormat: " + printer.getDocumentFormat());
			Log.d(tag, "Name: " + printer.getName());
			Log.d(tag, "RP: " + printer.getRp());
			Log.d(tag, "State: " + printer.getState());
			Log.d(tag, "Type: " + printer.getType());
			sPrinters += "(Printer: " + printer.getName() + "\n State: "
					+ printer.getState() + "\n Type: " + printer.getType()
					+ ")\n";			
			
//			TextView txt = (TextView) findViewById(R.id.name_printer);
			
			try {	
//				txt.setText(sPrinters);
//				Intent intent = new Intent(getApplicationContext(), PrintSelectPrinter.class);
//				intent.putParcelableArrayListExtra("listprinter",  arrayPrinter);
//				startActivity(intent);
			} catch (IllegalStateException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
		
		
	}
	
	public void LoadPrinterDefault(){
		Printer printers  ;
		printers = PrinterProviderMgMt.getDefaultPrinter();
		TextView txt = (TextView) findViewById(R.id.name_printer);
		try {
			if(PrinterProviderMgMt.getDefaultPrinter() != null){
				txt.setText(printers.getName());
				
				SaveOptionPrint.writeString(PrintApplication.getAppContext(), 
						SaveOptionPrint.NAME_PRINTER,
						printers.getName());
				Log.d("save", "save" + SaveOptionPrint.readString(PrintApplication.getAppContext(),
						SaveOptionPrint.NAME_PRINTER, 
						""));
			}
				
				txt.setText("");
			
		} catch (IllegalStateException e) {
			
			e.printStackTrace();
		}
	}

	public void testPrint() {
		// Test print
		File rootsd = Environment.getExternalStorageDirectory();
		File dcim = new File(rootsd.getAbsolutePath() + "/DCIM/nvan.jpg");
		List<String> imgPaths = new ArrayList<String>();

		Log.i("", dcim.getAbsolutePath());

		imgPaths.add(dcim.getAbsolutePath());
		Printer printer = PrinterProviderMgMt.getDefaultPrinter();
		if (printer == null) {
			Log.d(tag, "Printer is null");
			return;
		} else
			Log.d(tag, "Print...: " + printer.getName());

		print(PrinterProviderMgMt.getDefaultPrinter(), imgPaths);
	}

	public class ListReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// verify if the message we received is the one that we want
			if (intent.getAction().equals(
					PrinterDiscovery.PRINTER_RECEIVER_ACTION)) {

				// get the arrayList we have sent with intent (the one from
				// BroadcastSender)
				// notice that we needed the key "value"
				if (arrayOrgPrinters == null)
					arrayOrgPrinters = new ArrayList<Printer>();

				ArrayList<Printer> _arrPrinter = intent.getParcelableArrayListExtra(PrinterDiscovery.PRINTER_RECEIVER_KEY);
				Printer _printer = _arrPrinter.get(0);

				arrayOrgPrinters.add(_printer);

				Log.d(tag, "PRINTER_RECEIVER_ACTION");
				// Update printer state
				if (_arrayStatePrinters != null
						&& _arrayStatePrinters.size() > 0
						&& !_printer.getType().equals("epson")) {
					for (Printer statePrinter : _arrayStatePrinters) {
						// Compare by name & IP
						for (Printer orgPrinter : arrayOrgPrinters) {
							if (orgPrinter.equals(statePrinter)) {
								Log.d(tag, "Replace by printer had new state");
								// Replace by printer had new state
								Printer _newStatePrinter = arrayOrgPrinters
										.get(arrayOrgPrinters
												.indexOf(orgPrinter));
								_newStatePrinter.setState(statePrinter
										.getState());
								arrayOrgPrinters.set(
										arrayOrgPrinters.indexOf(orgPrinter),
										_newStatePrinter);
								break;
							}
						}
					}
				}

			} else if (intent.getAction().equals(
					PrinterDiscovery.PRINTER_STATE_RECEIVER_ACTION)) {
				_arrayStatePrinters = intent
						.getParcelableArrayListExtra(PrinterDiscovery.PRINTER_RECEIVER_CHECK_KEY);
				Log.d(tag, "PRINTER_STATE_RECEIVER_ACTION");
				// Update printer state
				if (arrayOrgPrinters != null && arrayOrgPrinters.size() > 0) {
					for (Printer printer : arrayOrgPrinters) {
						// Compare by name & IP
						for (Printer statePrinter : _arrayStatePrinters) {
							if (statePrinter.equals(printer)) {
								Log.d(tag, "Replace by printer had new state");
								// Replace by printer had new state
								Printer _newStatePrinter = arrayOrgPrinters
										.get(arrayOrgPrinters.indexOf(printer));
								_newStatePrinter.setState(statePrinter
										.getState());
								arrayOrgPrinters.set(
										arrayOrgPrinters.indexOf(printer),
										_newStatePrinter);
								break;
							}
						}
					}
				}
			}

//			PrinterProviderMgMt.deleteAll();
			Log.d("arrayOrgPrinters", "arrayOrgPrinters"+ arrayOrgPrinters);
			List<Uri> uris = PrinterProviderMgMt.insert(arrayOrgPrinters);
			Log.d(tag, "Number printer Inserted: " + uris.size());

			// Set default printer if array have size is 1
			if (arrayOrgPrinters != null && arrayOrgPrinters.size() == 1){
				PrinterProviderMgMt.setDefaultPrinter(arrayOrgPrinters.get(0));
				Log.d("default printer", "default" );
				
			}
			
			LoadPrinterDefault();
			//Check if current is print option screen
//			int screenActivity = 0;
//			PrintApplication application = new PrintApplication();
//			screenActivity = application.getScreenActivity();
//			Log.d("screen Activity", "screen Activity" + screenActivity);
//		    if(screenActivity == PRINT_SELECT_PRINTER){
//		    	Log.d("option", "load printer form Printer");
//		    	LoadPrinter();
//		    }
			
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			active = false;
		}
		return super.onTouchEvent(event);
	}

	// get id for button

	public void buttonView() {
		setOnClickListener(R.id.button_plushtech_document);
		setOnClickListener(R.id.button_plushtech_email);
		setOnClickListener(R.id.button_plushtech_photo);
		setOnClickListener(R.id.button_plushtech_webpage);
		setOnClickListener(R.id.button_setting);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_plushtech_document:
			document();
			break;

		case R.id.button_plushtech_email:
			email();
			break;
		case R.id.button_plushtech_photo:
			photoActivity();
			break;

		case R.id.button_setting:
			settingActivity();
			break;

		case R.id.button_plushtech_webpage:
			
			webpageActivity();
			break;

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			Bundle bResult = data.getExtras();
			String dataResult = "";
			PrintRequest pr = null;
			switch (requestCode) {
			case REQUEST_DOCUMENT:
				dataResult = bResult.getString(Common.INTENT_EXTRA_RESULT);
				pr = new PrintRequest(data.getExtras().getString(
						Common.INTENT_EXTRA_RESULT),
						Common.getFileType(dataResult));
				Common.startPrintPreview(BaseActivity.this, pr);
				break;
			case REQUEST_PHOTO:
				// Get image path
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				dataResult = cursor.getString(columnIndex);
				cursor.close();
				Log.d("image", "" + dataResult);
				pr = new PrintRequest(dataResult,
						Common.getFileType(dataResult));
				Common.startPrintPreview(BaseActivity.this, pr);
				break;
			case REQUEST_EMAIL:
				// TODO:
				break;
			case REQUEST_WEBPAGE:

				pr = (PrintRequest) bResult.getSerializable(Common.INTENT_EXTRA_RESULT);
				dataResult = pr.getFilePath();

				PrintRequest printRequest = new PrintRequest(dataResult,
						Common.getFileType(dataResult));
				Common.startPrintPreview(BaseActivity.this, printRequest);
				break;
			case REQUEST_SETTING:
				// TODO:
				break;
			default:
				break;
			}
		}
	}

	// call aler docment type

	public void document() {
		Intent it = new Intent(getBaseContext(), FileExplorerMain.class);
		startActivityForResult(it, REQUEST_DOCUMENT);
	}

	// intent email
	public void email() {
		PackageManager pm = getPackageManager();
		Intent intent = pm
				.getLaunchIntentForPackage("com.plustech.mem.activities");
		startActivityForResult(intent, REQUEST_EMAIL);
	}

	// intent photo
	public void photoActivity() {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, REQUEST_PHOTO);
	}

	// intent setting
	public void settingActivity() {
		Intent intent = new Intent(BaseActivity.this, PrintSeting.class);
		startActivityForResult(intent, REQUEST_SETTING);
		
//		screenActivity =1;
	}

	// iten webpage
	public void webpageActivity() {
		Intent intent = new Intent(PrintApplication.getAppContext(),WebActivity.class);
		startActivityForResult(intent, REQUEST_WEBPAGE);
	}

	// method get id for view
	private void setOnClickListener(int viewCode) {
		View thisView = findViewById(viewCode);
		if (thisView != null) {
			thisView.setOnClickListener(this);
		}
	}
	
	

}
