package com.plustech.print.congdongandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.plustech.print.BaseActivity;
import com.plustech.print.PrintApplication;
import com.plustech.print.R;
import com.plustech.print.SaveOptionPrint;
import com.plustech.print.drivers.discovery.Printer;
import com.plustech.print.storage.PrinterProviderMgMt;

public class PrintSeting extends BaseActivity implements OnClickListener {

	// setting value for printer
	private final CharSequence[] font = { " Small ", " Normal", " Large " };
	private final CharSequence[] scale = { " Originalsize ", " Fit To Page" };
	private final CharSequence[] margin = { " No Margins ", " 1/4", "1/3", "1/2" };
	private final CharSequence[] orienstation = { " Auto ", " Portrait", "Landscape" };
	private final CharSequence[] size = { " Letter ", " Legal", "A4" };
	
	public static final int PRINT_SELECT =1;
	
	
	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_print_setting);
		
		idView();
		optionPrinter();

	}
	
	
	public void idView(){
		setOnClickListener(R.id.item_fontsize);
		setOnClickListener(R.id.item_scale_picture);
		setOnClickListener(R.id.item_orienstation);
		setOnClickListener(R.id.item_page_magin);
		setOnClickListener(R.id.item_printer_setting);
		setOnClickListener(R.id.item_page_size);
		setOnClickListener(R.id.button_back_print_setting);
		
		setOnClickListener(R.id.linear_item_fontsize);
		setOnClickListener(R.id.linear_item_orienstation);
		setOnClickListener(R.id.linear_item_page_magin);
		setOnClickListener(R.id.linear_item_page_size);
		setOnClickListener(R.id.linear_item_scale_picture);
//		setOnClickListener(R.id.linear_item_printer_setting);
		
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
		case R.id.linear_item_fontsize:
			fontsizeSetting();
			break;
		
		case R.id.linear_item_orienstation:
			orienstationSetting();
			break;
			
		case R.id.linear_item_page_magin:	
			pagemaginSetting();
			break;
		
		case R.id.linear_item_page_size:
			pagesizeSetting();
			break;

		case R.id.item_printer_setting:
			printerSetting();
			break;
		case R.id.linear_item_scale_picture:
			scalepictureSetting();
			break;
		case R.id.button_back_print_setting:
			finish();
			break;
			
		}
		super.onClick(view);
	}
	
	// setting font size 
	public void fontsizeSetting(){
//		 final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
//		 final SharedPreferences.Editor editor = preferences.edit();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(PrintSeting.this);
		builder.setTitle("Fontsize");
		builder.setSingleChoiceItems(font, 0,
				new DialogInterface.OnClickListener() {			

					@Override
					public void onClick(DialogInterface dialog, int item) {
						switch (item) {
//						" Small ", " Normal", " Large "
						case 1:
							SaveOptionPrint.writeString(PrintApplication.getAppContext(), SaveOptionPrint.FONTSIZE, "Normal");
							break;
						case 2:
							SaveOptionPrint.writeString(PrintApplication.getAppContext(), SaveOptionPrint.FONTSIZE, "Large");
							break;							

						default:
							SaveOptionPrint.writeString(PrintApplication.getAppContext(), SaveOptionPrint.FONTSIZE, "Small");
							break;
						}

					}
				});
		builder.setPositiveButton("Apply",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						TextView textView = (TextView)findViewById(R.id.item_fontsize);
						
						textView.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(),
								SaveOptionPrint.FONTSIZE, 
								"small"));
						
//						textView.setText(preferences.getString(FONTSIZE, "small"));

					}
				});
		builder.setNegativeButton("Discard",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}
				});

		builder.show();
		
		
	}
	
	// setting orienstation
	public void orienstationSetting(){
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(PrintSeting.this);
		builder.setTitle("Page Orienstation");
		builder.setSingleChoiceItems(orienstation, -1,
				new DialogInterface.OnClickListener() {
//			Auto ", " Portrait", "Landscape"
					@Override
					public void onClick(DialogInterface dialog, int item) {
						switch (item) {
						case 0:
							SaveOptionPrint.writeString(PrintApplication.getAppContext(), SaveOptionPrint.ORIENSTATION, "Auto");
							break;
						case 1:
							SaveOptionPrint.writeString(PrintApplication.getAppContext(), SaveOptionPrint.ORIENSTATION, "Portrait");
							break;
						case 2:
							SaveOptionPrint.writeString(PrintApplication.getAppContext(), SaveOptionPrint.ORIENSTATION, "Landscape");
							break;

						}

					}
				});
		builder.setPositiveButton("Apply",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						TextView textView = (TextView)findViewById(R.id.item_orienstation);
						textView.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(), 
								SaveOptionPrint.ORIENSTATION, 
								"Auto"));

					}
				});
		builder.setNegativeButton("Discard",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}
				});

		builder.show();
		
	}
	
	// setting page magin
	public void pagemaginSetting(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(PrintSeting.this);
		builder.setTitle("Pages Margins");
		builder.setSingleChoiceItems(margin, 0,
				new DialogInterface.OnClickListener() {
//			No Margins ", " 1/4", "1/3", "1/2"
					@Override
					public void onClick(DialogInterface dialog, int item) {
						switch (item) {
						case 0:
							
							SaveOptionPrint.writeString(PrintApplication.getAppContext(), SaveOptionPrint.MARGIN, "No Margins");
							break;
						case 1:
							SaveOptionPrint.writeString(PrintApplication.getAppContext(), SaveOptionPrint.MARGIN, "1/4");
							break;
						case 2:
							SaveOptionPrint.writeString(PrintApplication.getAppContext(), SaveOptionPrint.MARGIN, "1/3");
							break;
						case 3:
							SaveOptionPrint.writeString(PrintApplication.getAppContext(), SaveOptionPrint.MARGIN, "1/2");
							break;

						default:
							break;
						}

					}
				});
		builder.setPositiveButton("Apply",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						TextView textView = (TextView)findViewById(R.id.item_page_magin);
						textView.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(), SaveOptionPrint.MARGIN, "No Magins"));

					}
				});
		builder.setNegativeButton("Discard",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}
				});

		builder.show();
		
		
	}
	// setting page size
	public void pagesizeSetting(){
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(PrintSeting.this);
		builder.setTitle("Paper Size");
		builder.setSingleChoiceItems(size, 0,
				new DialogInterface.OnClickListener() {
//			letter ", " Legal", "A4"
					@Override
					public void onClick(DialogInterface dialog, int item) {
						switch (item) {
						case 0:
							SaveOptionPrint.writeString(PrintApplication.getAppContext(), SaveOptionPrint.SIZE, "Letter");
							break;
						case 1:
							SaveOptionPrint.writeString(PrintApplication.getAppContext(), SaveOptionPrint.SIZE, "Legal");
							break;
						case 2:
							SaveOptionPrint.writeString(PrintApplication.getAppContext(), SaveOptionPrint.SIZE, "A4");
							break;

						}

					}
				});
		builder.setPositiveButton("Apply",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						TextView textView = (TextView)findViewById(R.id.item_page_size);
						textView.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(), SaveOptionPrint.SIZE, "Letter"));

					}
				});
		builder.setNegativeButton("Discard",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}
				});

		builder.show();
		
	}
	// setting printer
	public void printerSetting(){
		
//		PrintApplication application = new PrintApplication();
//		application.setScreenActivity(1);
//		Log.d("screen", "printSelect" + application.getScreenActivity());
		Intent intent = new Intent(PrintSeting.this, PrintSelectPrinter.class);
		startActivity(intent);
		
	}
	
	
	// setting scale_picture
	public void scalepictureSetting(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(PrintSeting.this);
		builder.setTitle("Scale Picture");
		builder.setSingleChoiceItems(scale, -1,
				new DialogInterface.OnClickListener() {
//			" Originalsize ", " Fit To Page" 
					@Override
					public void onClick(DialogInterface dialog, int item) {
						switch (item) {
						case 0:
							SaveOptionPrint.writeString(PrintApplication.getAppContext(), SaveOptionPrint.SCALE, "Originalsize");
							break;

						case 1:
							SaveOptionPrint.writeString(PrintApplication.getAppContext(), SaveOptionPrint.SCALE, "Fit To Page");
							
							break;
						}

					}
				});
		builder.setPositiveButton("Apply",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						TextView textView = (TextView)findViewById(R.id.item_scale_picture);
						textView.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(), SaveOptionPrint.SCALE, "Fit To Page"));

					}
				});
		builder.setNegativeButton("Discard",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}
				});

		builder.show();
	}
	
	public void optionPrinter(){
		
		TextView scale = (TextView)findViewById(R.id.item_scale_picture);
		scale.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(),
				SaveOptionPrint.SCALE, 
				"Oziginalsize"));
		
		TextView size = (TextView)findViewById(R.id.item_page_size);
		size.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(),
				SaveOptionPrint.SIZE, 
				"Legal"));
		
		TextView fontsize = (TextView)findViewById(R.id.item_fontsize);
		fontsize.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(),
				SaveOptionPrint.FONTSIZE, 
				"small"));
		
		TextView margin = (TextView)findViewById(R.id.item_page_magin);
		margin.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(),
				SaveOptionPrint.MARGIN, 
				"No Margins"));
		
		TextView orienstation = (TextView)findViewById(R.id.item_orienstation);
		orienstation.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(),
				SaveOptionPrint.ORIENSTATION, 
				"Portrait"));
		
		TextView nameprinter = (TextView)findViewById(R.id.item_printer_setting);
		nameprinter.setText(SaveOptionPrint.readString(PrintApplication.getAppContext(),
				SaveOptionPrint.NAME_PRINTER, "Canon"));
		
		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		optionPrinter();
		
	}

}
