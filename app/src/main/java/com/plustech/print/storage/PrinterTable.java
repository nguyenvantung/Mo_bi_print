package com.plustech.print.storage;

import android.net.Uri;
import android.provider.BaseColumns;

public class PrinterTable {

	public PrinterTable() {
    }
 
    public static final class PrinterTables implements BaseColumns {
        private PrinterTables() {
        }
 
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + PrinterProvider.AUTHORITY + "/printers");
 
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.plustech.printers";
 
        public static final String PRINTER_ID = "_id";
 
        public static final String NAME = "name";
 
        public static final String ADDRESS = "address";
        
        public static final String TYPE = "type";
        
        public static final String RP = "rp";
        
        public static final String DOCUMENT_FORMAT = "documentFormat";
        
        public static final String STATE = "state";
        
        //value = 1 is default printer
        public static final String DEFAULT_PRINTER = "defaultPrinter";
    }
}
