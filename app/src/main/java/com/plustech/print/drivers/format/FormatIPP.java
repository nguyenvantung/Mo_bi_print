/**
 * 
 */
package com.plustech.print.drivers.format;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.plustech.print.BaseActivity;
import com.plustech.print.PrintApplication;
import com.plustech.print.R;
import com.plustech.print.common.Common;
import com.plustech.print.common.PaperSizes.PaperSize_C;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;

/**
 * @author osd; Changed by NVAn
 */
public class FormatIPP {
	private static final String US_ASCII = "US-ASCII";
	private static final String UTF_8 = "UTF-8";

	private OutputStream out;

	private int requestID = 3;
	private String printerUri;
	private Context context;

	/*
	 * Document format of IPP printing values: "application/vnd.hp-PCL"
	 * "application/postscript" -> default
	 */
	private String documentFormat = "application/vnd.hp-PCL";

	public FormatIPP(Context context, OutputStream out, int requestID,
			String printerUri) {
		this.out = out;
		this.requestID = requestID;
		this.printerUri = printerUri;
		this.context = context;
	}

	/*
	 * Set document's format
	 */
	public void setDocumentFormat(String docFormat) {
		if (docFormat.equals("application/vnd.hp-PCL")) {
			// documentFormat = docFormat;
			requestID = 3;
		} else {
			documentFormat = "application/postscript";
			requestID = 2;
		}
	}

	/*
	 * Write a attribute to OutputStream
	 */
	private void writeAttribute(int valueTag, String name, String value)
			throws IOException {
		out.write(valueTag);
		// length of name
		out.write((name.length() >> 8) & 0xff);
		out.write(name.length() & 0xff);
		// name
		out.write(name.getBytes(UTF_8));
		// length of value
		out.write((value.length() >> 8) & 0xff);
		out.write(value.length() & 0xff);
		// value
		out.write(value.getBytes(UTF_8));
	}

	private void writeAttribute(int valueTag, String name, int value)
			throws IOException {
		out.write(valueTag);
		// length of name
		out.write((name.length() >> 8) & 0xff);
		out.write(name.length() & 0xff);
		// name
		out.write(name.getBytes(UTF_8));
		// length of int = 4
		out.write(4);
		// value
		out.write(0);
		out.write(0);
		out.write((value >> 8) & 0xff);
		out.write(value);
	}

	private boolean formatIPPPrintJobRequest() {
		try {
			Log.i(this.getClass().getName(), "Begin IPP job request");
			// version-number
			out.write(0x01);
			out.write(0x01);
			// operation-id
			out.write(0x00);
			out.write(0x02);
			// request-id
			out.write((int) ((requestID >> 24) & 0xff));
			out.write((int) ((requestID >> 16) & 0xff));
			out.write((int) ((requestID >> 8) & 0xff));
			out.write((int) (requestID & 0xff));

			// operation-attributes-tag
			out.write(0x01);
			// attributes-charset
			writeAttribute(0x47, "attributes-charset", "utf-8");
			// attributes-natural-language
			writeAttribute(0x48, "attributes-natural-language", "en-us");
			// printer-uri
			writeAttribute(0x45, "printer-uri", printerUri);
			// job-name
			writeAttribute(0x42, "job-name", "PlusTechPrint image");
			// requesting-user-name
			writeAttribute(0x42, "requesting-user-name", "PlusTechPrint");
			// document-format
			writeAttribute(0x49, "document-format", documentFormat);

			// job-attributes-tag
			out.write(0x02);
			/*
			 * //copies writeAttribute(0x21, "copies", numberOfCopies);
			 */
			// media
			writeAttribute(0x44, "media", "Letter");

			// end-of-attributes-tag
			out.write(0x03);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(this.getClass().getName(), "Return false");
			return false;
		}

		Log.i(this.getClass().getName(), "Return true");

		return true;
	}

	public boolean formatIPPForText(String srcString, PaperSize_C paperSize,
			int numberOfCopies) {
		if (out == null)
			return false;
		if (formatIPPPrintJobRequest() == false)
			return false;
		if (documentFormat.equals("application/vnd.hp-PCL")) {
			// Using PCL format
			Log.i("FormatIPP ForText - setNumberofcopies", "" + numberOfCopies);
			FormatPCL formatPCL = new FormatPCL(this.context, out);
			return formatPCL.formatPCLForText(srcString, paperSize,
					numberOfCopies);
		} else {
			// Using postscript format
			FormatPS formatPS = new FormatPS(this.context, out);
			return formatPS.formatPSForText(srcString, paperSize,
					numberOfCopies);
		}

	}

	public boolean formatIPPForImage(PaperSize_C paperSize, int numberOfCopies,
			String[] paths) {
		boolean res = false;
		int pageNum=0;
		
		if (out == null)
			return false;

		if (formatIPPPrintJobRequest() == false)
			return false;
		if (documentFormat.equals("application/vnd.hp-PCL")) {
			// Using PCL format
			FormatPCL formatPCL = new FormatPCL(this.context, out);
			for (String string : paths) {
				pageNum ++;
				Common.sendProgressState(String
						.format(PrintApplication.getAppContext().getString(
								R.string.printing_status), pageNum), false);
				
				res = formatPCL.formatPCLForImage(string, paperSize,
						numberOfCopies, false);
			}
			
			Common.sendProgressState("", true);
			
			return res;
		} else {
			// Using postscript format
			FormatPS formatPS = new FormatPS(this.context, out);
			
			for (String string : paths) {
				pageNum ++;
				Common.sendProgressState(String
						.format(PrintApplication.getAppContext().getString(
								R.string.printing_status), pageNum), false);
				res = formatPS.formatPSForImage(paperSize, numberOfCopies,
						string);
				Common.sendProgressState("", true);
			}
			return res;
		}

	}

}
