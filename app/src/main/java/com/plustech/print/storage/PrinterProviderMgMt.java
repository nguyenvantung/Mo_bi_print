package com.plustech.print.storage;

import java.util.ArrayList;
import java.util.List;

import android.R.string;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.plustech.print.PrintApplication;
import com.plustech.print.drivers.discovery.Printer;
import com.plustech.print.storage.PrinterTable.PrinterTables;

public class PrinterProviderMgMt {
	
	public static String TAG = PrinterProviderMgMt.class.getSimpleName();
	

	public static String[] PROJECTION = new String[] {
			PrinterTables.PRINTER_ID, PrinterTables.NAME,
			PrinterTables.ADDRESS, PrinterTables.TYPE, PrinterTables.RP,
			PrinterTables.DOCUMENT_FORMAT, PrinterTables.STATE,
			PrinterTables.DEFAULT_PRINTER };

	/**
	 * Insert a list printer
	 * 
	 * @param printers
	 * @return
	 */
	public static List<Uri> insert(List<Printer> printers) {

		if (printers == null) {
			Log.d("PrinterProviderMgMt", "List<printer> for insert is null");
			return new ArrayList<Uri>();
		}
		ContentResolver db = PrintApplication.getAppContext().getContentResolver();
		
		Cursor cursor= db.query(PrinterTables.CONTENT_URI, null, null, null, null);
		Log.d("cusordata", "cusordata" + cursor.getCount());			
		
		List<Uri> lsUri = new ArrayList<Uri>();
		ContentValues values = new ContentValues();	
		
		for (int i = 0; i < printers.size(); i++) {
			if (cursor.getCount() > 0) {
					
					while (cursor.moveToNext()) {
						String name = cursor.getString(cursor.getColumnIndex(PrinterTables.NAME));	
						Log.d("name", "name" + name.toString());
						Log.d("nameprint", "name" + printers.get(i).getName());
						if(printers.get(i).getName().equals(name)){
							Log.d("compare", "compare");
							return new ArrayList<Uri>();
							
							}
					}
						
						for (Printer printer : printers) {
							Uri uri;
							values = new ContentValues();
							values.put(PrinterTables.NAME, printer.getName());
							values.put(PrinterTables.ADDRESS, printer.getAddress());
							values.put(PrinterTables.TYPE, printer.getType());
							values.put(PrinterTables.RP, printer.getRp());
							values.put(PrinterTables.DOCUMENT_FORMAT,printer.getDocumentFormat());
							values.put(PrinterTables.STATE, printer.getState());
							values.put(PrinterTables.DEFAULT_PRINTER,printer.getDefaultPrinter());

							uri = db.insert(PrinterTables.CONTENT_URI, values);

							lsUri.add(uri);
							Log.d("listprinter1", "listprinter" );
						
						}
						
					
				
				

			}else if(cursor.getCount() == 0){
				for (Printer printer : printers) {
					Uri uri;
					values = new ContentValues();
					values.put(PrinterTables.NAME, printer.getName());
					values.put(PrinterTables.ADDRESS, printer.getAddress());
					values.put(PrinterTables.TYPE, printer.getType());
					values.put(PrinterTables.RP, printer.getRp());
					values.put(PrinterTables.DOCUMENT_FORMAT,printer.getDocumentFormat());
					values.put(PrinterTables.STATE, printer.getState());
					values.put(PrinterTables.DEFAULT_PRINTER,printer.getDefaultPrinter());

					uri = db.insert(PrinterTables.CONTENT_URI, values);

					lsUri.add(uri);
					Log.d("listprinter2", "listprinter" );
				}
				
			}
			
			
			
		}

		return lsUri;

	}

	/**
	 * Insert a printer
	 * 
	 * @param printer
	 * @return
	 */
	public static Uri insert(Printer printer) {
		ContentValues values = new ContentValues();
		values.put(PrinterTables.NAME, printer.getName());
		values.put(PrinterTables.ADDRESS, printer.getAddress());
		values.put(PrinterTables.TYPE, printer.getType());
		values.put(PrinterTables.RP, printer.getRp());
		values.put(PrinterTables.DOCUMENT_FORMAT, printer.getDocumentFormat());
		values.put(PrinterTables.STATE, printer.getState());
		values.put(PrinterTables.DEFAULT_PRINTER, printer.getDefaultPrinter());
		ContentResolver db = PrintApplication.getAppContext().getContentResolver();
		Uri uri = db.insert(PrinterTables.CONTENT_URI, values);

		return uri;
	}

	/**
	 * Delete a printer
	 * 
	 * @param printer
	 * @return
	 */
	public static int delete(Printer printer) {
		ContentResolver db = PrintApplication.getAppContext().getContentResolver();

		String where = PrinterTables._ID + " = ? ";
		String[] sectionArgs = new String[] { printer.get_id() };

		int iRows = db.delete(PrinterTables.CONTENT_URI, where, sectionArgs);

		return iRows;
	}

	/**
	 * Delete all printers
	 * 
	 * @return
	 */
	public static int deleteAll() {
		ContentResolver db = PrintApplication.getAppContext().getContentResolver();

		int iRows = db.delete(PrinterTables.CONTENT_URI, null, null);

		return iRows;
	}

	/**
	 * Update printer flow _id
	 * 
	 * @param printer
	 * @return
	 */
	public static int update(Printer printer) {

		String where = PrinterTables._ID + " = ? ";
		String[] selectionArgs = new String[] { printer.get_id() };

		ContentResolver db = PrintApplication.getAppContext().getContentResolver();
		ContentValues values = new ContentValues();
		values.put(PrinterTables.NAME, printer.getName());
		values.put(PrinterTables.ADDRESS, printer.getAddress());
		values.put(PrinterTables.TYPE, printer.getType());
		values.put(PrinterTables.RP, printer.getRp());
		values.put(PrinterTables.DOCUMENT_FORMAT, printer.getDocumentFormat());
		values.put(PrinterTables.STATE, printer.getState());
		values.put(PrinterTables.DEFAULT_PRINTER, printer.getDefaultPrinter());
		int iRows = db.update(PrinterTables.CONTENT_URI, values, where,	selectionArgs);

		return iRows;
	}

	/**
	 * Set default printer
	 * 
	 * @param printer
	 * @return
	 */
	public static int setDefaultPrinter(Printer printer) {
		Log.i("", "reset default printer");
		// reset default printer
		String where = PrinterTables.DEFAULT_PRINTER + " = ? ";
		String[] selectionArgs = new String[] { "1" };

		ContentResolver db = PrintApplication.getAppContext().getContentResolver();

		ContentValues values = new ContentValues();
		values.put(PrinterTables.DEFAULT_PRINTER, "0");

		int iRows = db.update(PrinterTables.CONTENT_URI, values, where,	selectionArgs);

		String selection = PrinterTables.ADDRESS + " = ? AND "+ PrinterTables.NAME + " = ? ";
		List<Printer> printers = PrinterProviderMgMt.select(selection, new String[] {
				printer.getAddress().trim(), printer.getName().trim() });
		printer = printers.get(0);
		// Set default printer
		where = PrinterTables._ID + " = ? ";

		selectionArgs = new String[] { printer.get_id() };

		values = new ContentValues();
		values.put(PrinterTables.DEFAULT_PRINTER, "1");

		iRows = db.update(PrinterTables.CONTENT_URI, values, where,	selectionArgs);
		Log.i("", "Set default printer: " + iRows);
		return iRows;
	}

	/**
	 * Get default printer
	 * 
	 * @return null if not exist
	 */
	public static Printer getDefaultPrinter() {

		// get default printer
		String selection = PrinterTables.DEFAULT_PRINTER + " = '1' ";
//		String[] selectionArgs = new String[] { "1" };
		
		Log.d("Printer Default", "Printer Default" + selection);

		ContentResolver db = PrintApplication.getAppContext().getContentResolver();

		Cursor c = db.query(PrinterTables.CONTENT_URI, PROJECTION, selection,null, null);

		Printer printer = null;
		if (c.moveToFirst()) {
			Log.i("", "move to first");
			printer = new Printer();
			printer.setName(c.getString(c.getColumnIndex(PrinterTables.NAME)));
			printer.setAddress(c.getString(c.getColumnIndex(PrinterTables.ADDRESS)));
			printer.setType(c.getString(c.getColumnIndex(PrinterTables.TYPE)));
			printer.setRp(c.getString(c.getColumnIndex(PrinterTables.RP)));
			printer.setDocumentFormat(c.getString(c.getColumnIndex(PrinterTables.DOCUMENT_FORMAT)));
			printer.setState(c.getString(c.getColumnIndex(PrinterTables.STATE)));
			printer.setDefaultPrinter(c.getString(c.getColumnIndex(PrinterTables.DEFAULT_PRINTER)));

			Log.i("", "move to first: " + printer.getName());

		}
		c.close();
		return printer;
	}

	/**
	 * Select all printer
	 * 
	 * @return
	 */
	public static ArrayList<Printer> selectAll() {
		ContentResolver db = PrintApplication.getAppContext().getContentResolver();

		Cursor c = db.query(PrinterTables.CONTENT_URI, null, null, null, null);
		Printer printer;
		ArrayList<Printer> printers = new ArrayList<Printer>();
		if (c.moveToFirst()) {
			do {
				printer = new Printer();
				printer.setName(c.getString(c.getColumnIndex(PrinterTables.NAME)));
				printer.setAddress(c.getString(c.getColumnIndex(PrinterTables.ADDRESS)));
				printer.setType(c.getString(c.getColumnIndex(PrinterTables.TYPE)));
				printer.setRp(c.getString(c.getColumnIndex(PrinterTables.RP)));
				printer.setDocumentFormat(c.getString(c.getColumnIndex(PrinterTables.DOCUMENT_FORMAT)));
				printer.setState(c.getString(c.getColumnIndex(PrinterTables.STATE)));
				printer.setDefaultPrinter(c.getString(c.getColumnIndex(PrinterTables.DEFAULT_PRINTER)));

				Log.i("","Test: "+ c.getString(c.getColumnIndex(PrinterTables.NAME)));

				printers.add(printer);
			} while (c.moveToNext());
		}

		c.close();
		Log.d(TAG, "Message"+ printers);
		
		return printers;
	}

	/**
	 * 
	 * @param selection
	 *            : example PrinterTables.ADDRESS + " LIKE ? "
	 * @param sectionArgs
	 *            : new String[]{"192.168.1.100"}
	 * @return
	 */
	public static List<Printer> select(String selection, String[] sectionArgs) {
		ContentResolver db = PrintApplication.getAppContext().getContentResolver();

		Cursor c = db.query(PrinterTables.CONTENT_URI, PROJECTION, selection,sectionArgs, null);
		Printer printer;
		List<Printer> printers = new ArrayList<Printer>();
		if (c.moveToFirst()) {
			do {
				printer = new Printer();
				printer.set_id(c.getString(c
						.getColumnIndex(PrinterTables.PRINTER_ID)));
				printer.setName(c.getString(c
						.getColumnIndex(PrinterTables.NAME)));
				printer.setAddress(c.getString(c
						.getColumnIndex(PrinterTables.ADDRESS)));
				printer.setType(c.getString(c
						.getColumnIndex(PrinterTables.TYPE)));
				printer.setRp(c.getString(c.getColumnIndex(PrinterTables.RP)));
				printer.setDocumentFormat(c.getString(c
						.getColumnIndex(PrinterTables.DOCUMENT_FORMAT)));
				printer.setState(c.getString(c
						.getColumnIndex(PrinterTables.STATE)));
				printer.setDefaultPrinter(c.getString(c
						.getColumnIndex(PrinterTables.DEFAULT_PRINTER)));
				printers.add(printer);
			} while (c.moveToNext());
		}

		c.close();
		return printers;
	}

}
