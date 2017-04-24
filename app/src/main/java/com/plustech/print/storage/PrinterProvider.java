package com.plustech.print.storage;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import com.plustech.print.storage.PrinterTable.PrinterTables;

public class PrinterProvider extends ContentProvider {

	private static final String TAG = "PrinterProvider";

	private static final String DATABASE_NAME = "printers.db";

	private static final int DATABASE_VERSION = 1;

	private static final String PRINTERS_TABLE_NAME = "printers";

	public static final String AUTHORITY = "com.plustech.print.storage.PrinterProvider";

	private static final UriMatcher sUriMatcher;

	private static final int PRINTERS = 1;

	private static final int PRINTER_ID = 2;

	private static HashMap<String, String> printersProjectionMap;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + PRINTERS_TABLE_NAME + " ("
					+ PrinterTables.PRINTER_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ PrinterTables.NAME + " VARCHAR(255),"
					+ PrinterTables.ADDRESS + " VARCHAR(255),"
					+ PrinterTables.TYPE + " VARCHAR(255),"
					+ PrinterTables.RP + " VARCHAR(255),"
					+ PrinterTables.DOCUMENT_FORMAT + " VARCHAR(255),"
					+ PrinterTables.STATE + " VARCHAR(255),"
					+ PrinterTables.DEFAULT_PRINTER + " VARCHAR(255)" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + PRINTERS_TABLE_NAME);
			onCreate(db);
		}
	}

	private DatabaseHelper dbHelper;


	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
		case PRINTERS:
			break;
		case PRINTER_ID:
			where = where + "_id = " + uri.getLastPathSegment();
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		int count = db.delete(PRINTERS_TABLE_NAME, where, whereArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case PRINTERS:
			return PrinterTables.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sUriMatcher.match(uri) != PRINTERS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long rowId = db.insert(PRINTERS_TABLE_NAME, PrinterTables.NAME, values);
		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(PrinterTables.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(PRINTERS_TABLE_NAME);
		qb.setProjectionMap(printersProjectionMap);

		switch (sUriMatcher.match(uri)) {
		case PRINTERS:
			break;
		case PRINTER_ID:
			selection = selection + "_id = " + uri.getLastPathSegment();
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case PRINTERS:
			count = db.update(PRINTERS_TABLE_NAME, values, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, PRINTERS_TABLE_NAME, PRINTERS);
		sUriMatcher.addURI(AUTHORITY, PRINTERS_TABLE_NAME + "/#", PRINTER_ID);
		
		printersProjectionMap = new HashMap<String, String>();
		printersProjectionMap.put(PrinterTables.PRINTER_ID, PrinterTables.PRINTER_ID);
		printersProjectionMap.put(PrinterTables.NAME, PrinterTables.NAME);
		printersProjectionMap.put(PrinterTables.ADDRESS, PrinterTables.ADDRESS);
		printersProjectionMap.put(PrinterTables.TYPE, PrinterTables.TYPE);
		printersProjectionMap.put(PrinterTables.RP, PrinterTables.RP);
		printersProjectionMap.put(PrinterTables.DOCUMENT_FORMAT, PrinterTables.DOCUMENT_FORMAT);
		printersProjectionMap.put(PrinterTables.STATE, PrinterTables.STATE);
		printersProjectionMap.put(PrinterTables.DEFAULT_PRINTER, PrinterTables.DEFAULT_PRINTER);
	}
}