package com.plustech.print.congdongandroid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmc.osd.ndk.bitmap.BitmapHolder;
import com.plustech.print.BaseActivity;
import com.plustech.print.PrintApplication;
import com.plustech.print.R;
import com.plustech.print.SaveOptionPrint;
import com.plustech.print.common.Common;
import com.plustech.print.object.PrintRequest;
import com.plustech.print.storage.PrinterProviderMgMt;

public class PrintPreviewImage extends BaseActivity implements OnClickListener {

	protected static String TAG = PrintPreviewImage.class.getSimpleName();

	@SuppressWarnings("deprecation")
	private Gallery gallery;
	public static File imageFile;
	int[] count;
	protected PrintRequest pr;
	
	
	public int progressBarStatus ;
	public long size;
	public ProgressDialog progressBar;

	private Handler progressBarbHandler = new Handler();
    private EditText numberPrintEditText, numberCopyEditText;

	private String number_page;
	private String number_copy;
	

	@SuppressLint("CutPasteId")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_print_preview);
		// set value for printer
		printOption();
		

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			pr = (PrintRequest) bundle.getSerializable("data");
			Log.d(TAG, "Print request path file: " + pr.getFilePath());
			switch (Common.FILE_TYPE.valueOf(pr.getFileType())) {
			case TYPE_PDF:
				// PDF
				//TODO: Convert pdf to image and then
				//imageFile = new File(path_to_image_file);
				break;
			case TYPE_DOC:
				// Doc
				//TODO: Convert Doc to image and then
				//imageFile = new File(path_to_image_file);
				break;
			case TYPE_DOCX:
				// Docx
				//TODO: Convert docx to image and then
				//imageFile = new File(path_to_image_file);
				break;
			case TYPE_XLS:
				// xls
				//TODO: Convert xls to image and then
				//imageFile = new File(path_to_image_file);
				break;
			case TYPE_XLSX:
				// xlsx
				//TODO: Convert xlsx to image and then
				//imageFile = new File(path_to_image_file);
				break;
			case TYPE_TXT:
				// txt
				//TODO: Convert txt to image and then
				//imageFile = new File(path_to_image_file);
				break;
			case TYPE_IMAGE:
				// image
				imageFile = new File(pr.getFilePath());
				break;
			default:
				break;
			}
		}
		
		

		buttonView();
		// get value form edit text

		
		gallery = (Gallery) findViewById(R.id.galley_image);

		gallery = (Gallery) findViewById(R.id.galley_image);
		gallery.setSpacing(1);
		gallery.setAdapter(new GalleryImageAdapter(this, imageFile));
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				imageFull();
			}
		});
		
		
		

	}

	public void buttonView() {
		setOnClickListener(R.id.button_back);
		setOnClickListener(R.id.button_print_preview);
	}

	// method get id for view
	private void setOnClickListener(int viewCode) {
		View thisView = findViewById(viewCode);
		if (thisView != null) {
			thisView.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.button_print_preview:
			printImage();
			break;
		case R.id.button_back:
			finish();
			break;

		}
		super.onClick(view);
	}

	// view full screen activity
	public void imageFull() {

		Intent intent = new Intent(getApplicationContext(),PrintPreviewFull.class);
		intent.putExtra("image_position", imageFile.toString());
		startActivity(intent);

	}
	
	@SuppressLint("CutPasteId")
	public void printOption(){
		
		// set value for printer
		TextView scalePicture = (TextView)findViewById(R.id.print_fittopage);
		scalePicture.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(), 
				SaveOptionPrint.SCALE, "Originalsize"));
		
		TextView fontsize = (TextView)findViewById(R.id.print_size);
		fontsize.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(), 
				SaveOptionPrint.FONTSIZE, "Normal"));
		
		TextView mardins = (TextView)findViewById(R.id.print_page_margin);
		mardins.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(), 
				SaveOptionPrint.MARGIN, "No Margins"));
		
		TextView size = (TextView)findViewById(R.id.print_size);
		size.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(), 
				SaveOptionPrint.SIZE, "Legal"));
		
		TextView orienstation = (TextView)findViewById(R.id.print_orienstation);
		orienstation.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(), 
				SaveOptionPrint.ORIENSTATION, "Portrait"));
		
		TextView name_printer = (TextView)findViewById(R.id.print_name_printer);
		name_printer.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(),
				SaveOptionPrint.NAME_PRINTER, ""));
		
	}
	// get value form Page and number copy
	
	public void PrintOptinEdit(){
		// save value form edit number page
		numberPrintEditText = (EditText)findViewById(R.id.edit_number_print);
		number_page = numberPrintEditText.getText().toString().trim();
		SaveOptionPrint.writeString(PrintApplication.getAppContext(),
				SaveOptionPrint.NUMBER_PAGE, number_page);
		// save vualue form edit number copy
		numberCopyEditText = (EditText)findViewById(R.id.edit_number_copy);
		number_copy = numberCopyEditText.getText().toString().trim();
		SaveOptionPrint.writeString(PrintApplication.getAppContext(),
				SaveOptionPrint.NUMBER_COPY, number_copy);
		
	}
// method print image
	public void printImage() {
		
		PrintOptinEdit();
		final List<String> imagePath = new ArrayList<String>();
		imagePath.add(imageFile.toString());
		
		progressBar = new ProgressDialog(PrintPreviewImage.this);
		progressBar.setCancelable(true);
		progressBar.setMessage("Print Progress...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.setMax(100);
		progressBar.show();

		progressBarStatus = 0;

		size = 0;

		new Thread(new Runnable() {

			public void run() {
				while (progressBarStatus < 100) {

					// process some tasks
					progressBarStatus = Progressfile();
					
					print(PrinterProviderMgMt.getDefaultPrinter(), imagePath);
					Log.d("printer", "printer" + imagePath.add(imageFile.toString()));

					// sleep 1 second (simulating a time consuming task...)
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Update the progress bar
					progressBarbHandler.post(new Runnable() {
						public void run() {
							progressBar.setProgress(progressBarStatus);
						}
					});
				}

				// if the file is downloaded,
				if (progressBarStatus >= 100) {

					// sleep 2 seconds, so that you can see the 100%
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// and then close the progressbar dialog
					progressBar.dismiss();
				}
			}
		}).start();

	

	}
	
	
	public int Progressfile() {

		while (size <= 1000000) {

			size++;

			if (size == 100000) {
				return 10;
				
			} else if (size == 200000) {
				return 20;
				
			} else if (size == 300000) {
				return 30;

			} else if (size == 400000) {
				return 40;

			} else if (size == 500000) {
				
				return 50;
			} else if (size == 700000) {
				
				return 70;
			} else if (size == 800000) {
				
				return 80;
			}
			//...

		}

		return 100;

	}

	// adapter gridview
	public class GalleryImageAdapter extends BaseAdapter {
		private Context mContext;
		File mfile;

		public GalleryImageAdapter(Context context, File file) {
			mContext = context;
			mfile = file;
		}

		public int getCount() {

			return 1;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		// Override this method according to your need
		public View getView(int index, View view, ViewGroup viewGroup) {

			ImageView i = new ImageView(mContext);
			Log.d("path", "path" + mfile);

			// i.setImageBitmap(BitmapFactory.decodeFile(mfile.getAbsolutePath()));

			if (mfile != null) {

				BitmapHolder holder = new BitmapHolder(
						PrintApplication.getAppContext());
				Bitmap bitmap = holder.decodeLargeFiles(getBaseContext(), mfile.getAbsoluteFile().toString());

				i.setImageBitmap(bitmap);

				Log.d("path", "bitmap" + mfile.getAbsolutePath());
				i.setLayoutParams(new Gallery.LayoutParams(200, 200));
				i.setScaleType(ImageView.ScaleType.FIT_XY);
			}else{
				Log.d("PrintPreviewImage", "Can not get file");
			}
			return i;
		}
	}

	class ViewHolder {
		ImageView imageview;
		int id;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
