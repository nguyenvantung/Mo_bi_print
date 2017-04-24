package com.plustech.print.congdongandroid;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.plustech.print.BaseActivity;
import com.plustech.print.PrintApplication;
import com.plustech.print.R;
import com.plustech.print.R.color;
import com.plustech.print.SaveOptionPrint;
import com.plustech.print.drivers.discovery.Printer;
import com.plustech.print.storage.PrinterProviderMgMt;

public class PrintSelectPrinter extends BaseActivity implements OnClickListener{
	
	public PrinterProviderMgMt printer = new PrinterProviderMgMt();
	public Context context;
	public ListView listView;
	
	// list printer ap auto connect
	public List<Printer> aPrinters;
	// list printer ad manual
	public ArrayList<Printer> arrayPrintersManual = new ArrayList<Printer>();
	private Button scanPrinterButton, adManualButton, disCardButton;
	
	public String namePrinter, IpPrinter;
	public  AdaperPrinter adaperPrinter;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_slectprinter);
//		aPrinters = this.getIntent().getParcelableArrayListExtra("listprinter");
		aPrinters = PrinterProviderMgMt.selectAll();
		initView();		
		adViewButton();
		listView = (ListView)findViewById(R.id.settings_printer_list_view);	
		
		 adaperPrinter = new AdaperPrinter(PrintSelectPrinter.this, aPrinters);
		 listView.setAdapter(adaperPrinter);
		 
		 
		
	}
	
	public void adViewButton(){
		scanPrinterButton = (Button)findViewById(R.id.scanfor_printer);
		scanPrinterButton.setOnClickListener(this);
		adManualButton = (Button)findViewById(R.id.manual);
		adManualButton.setOnClickListener(this);
		disCardButton = (Button)findViewById(R.id.discard_priter);
		disCardButton.setOnClickListener(this);
	}
	
	public void initView(){
		setOnClickListener(R.id.back_selectprint);
		setOnClickListener(R.id.number_printer);
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
		case R.id.back_selectprint:
			finish();
			break;
		case R.id.discard_priter:
			Toast.makeText(getApplicationContext(), "Discard", Toast.LENGTH_LONG).show();
			break;
		case R.id.scanfor_printer:
			Toast.makeText(getApplicationContext(), "scan_printer", Toast.LENGTH_LONG).show();
			break;
		case R.id.manual:
			Log.d("manual", "manual");
			Addmanual();
			
			break;

		default:
			break;
		}
		
	}
	
	// add printer manual
	
	public void Addmanual(){
		 final Dialog dialog = new Dialog(PrintSelectPrinter.this);
			dialog.setContentView(R.layout.layout_selectprinter_admanual);
			dialog.setTitle("Manual");
			final EditText name_printer = (EditText)dialog.findViewById(R.id.manual_name);
			final EditText ip1 = (EditText)dialog.findViewById(R.id.manual_IP1);
			final EditText ip2 = (EditText)dialog.findViewById(R.id.manual_IP2);
			final EditText ip3 = (EditText)dialog.findViewById(R.id.manual_IP3);
			final EditText ip4 = (EditText)dialog.findViewById(R.id.manual_IP4);
			Button test_Button = (Button)dialog.findViewById(R.id.manual_test);
			test_Button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					if(ip1.getText().toString().equals("")
							||ip2.getText().toString().equals("")
							||ip3.getText().toString().equals("")
							||ip4.getText().toString().equals("")){
						
					AlertDialog.Builder builder = new AlertDialog.Builder(PrintSelectPrinter.this);
					builder.setTitle("Message Info");
					builder.setMessage("again");
					builder.setPositiveButton("Continue",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,int which) {
									return;
								}
							});
					builder.show();

					}else{
						namePrinter = name_printer.getText().toString().trim();
						IpPrinter = ip1.getText().toString().trim() + "." +
								    ip2.getText().toString().trim() + "." +
								    ip3.getText().toString().trim() + "." +
								    ip4.getText().toString().trim();
						Printer printer = new Printer();
						printer.setName(namePrinter);
						printer.setAddress(IpPrinter);
						
						arrayPrintersManual.add(printer);
						
						PrinterProviderMgMt.insert(arrayPrintersManual);
						// adaperPrinter.notifyDataSetChanged();
						 adaperPrinter.addItem(printer);
						dialog.cancel();
						
						
//						Intent intent = new Intent(PrintSelectPrinter.this, PrintSelectPrinter.class);
//						startActivity(intent);
					}

				}
			});
			dialog.show();
		
		
	}
	
	
	
	public class AdaperPrinter extends BaseAdapter{
		public LayoutInflater mInflater;
		private List<Printer> list;
		
		public AdaperPrinter(Context mcontext, List<Printer> printers){
			list = printers;
			context = mcontext;
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
//			TextView textView = (TextView)findViewById(id)
			Log.d("count", "count" + list.size());
			return list.size();
		}
		
		public void addItem(Printer item) {
//			
//			
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i).getName().equals(item.getName())){
					Log.d("abc", "abc");
					break;
				}
				Log.d("abcdef", "abc");
				list.add(item);
				break;
				
			}
//			notifyDataSetChanged();
			
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.layout_slectprinter_item, null);
				holder.textView = (TextView)convertView.findViewById(R.id.item_name_printer);
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			final Printer printer = list.get(position);
			if(printer != null){
				holder.id = position;
				holder.textView.setText(printer.getName());
				
			}
			holder.textView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					printer.getName();
					Log.d("nameprinter", "nameprint_list" + printer.getName());
					SaveOptionPrint.writeString(PrintApplication.getAppContext(),
							SaveOptionPrint.NAME_PRINTER, printer.getName());
					
				}
			});
			return convertView;
		}
		
	}
	
	public class ViewHolder{
		int id;
		TextView textView;
	}
	
}
