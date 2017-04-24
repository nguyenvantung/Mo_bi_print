/**
 * 
 */
package com.plustech.print.drivers.format;

import org.apache.pdfbox.io.ASCII85OutputStream;

import com.plustech.print.common.PaperSizes.PaperSize_C;
import com.plustech.print.reader.ImageReader;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;


/**
 * @author  osd 
 *
 */
public class FormatPS {
	private Context context;
	String tag = "FormatPS";
	
	public FormatPS(Context context, OutputStream out) {
        this.out = out;
        this.context = context;
    }
	
	private static final String UTF_8 = "UTF-8";
	
	private OutputStream out;
	
	private String getPSFontName(String userFont)
	{
		String font = "Courier";
		return font;
	}
	
	private void writeString(String str) throws IOException {
		out.write(str.getBytes(UTF_8));
    }
	 
	private void lineStart(int x, int y) throws IOException{   
		String lineStart = 	((Integer)x).toString() + " " +
							((Integer)y).toString() + " moveto (";
		writeString(lineStart);
    }
	
	private void lineEnd() throws IOException{   
		writeString(") show\n");
    }
	
	private void showPage() throws IOException{   
		writeString("showpage\n");
    }
	
	private static final int inchInPoints = 72;
    private static final int marginPoints = 40;
    private static final double marginWidth = 18.0;
    private static final double marginHeight = 9.0;

    private static final int kCourier10CharPerInch = 12;
//	    private static final int kCourier12CharPerInch = 10;

    private static final int kSingleSpacing = 20;
	    
	public boolean formatPSForText(String srcString, PaperSize_C paperSize, int numberOfCopies){
		float widthOut = paperSize.inchWidth;
		float heightOut = paperSize.inchHeight;
		int charsPerLineMax = (int)((widthOut - 1) * kCourier10CharPerInch);
		int firstLinePos = (int)(heightOut*inchInPoints) - marginPoints;
		int linesPerPageMax = (firstLinePos - marginPoints)/kSingleSpacing;

		String header_p2 = getPSFontName("") + " findfont\n10 scalefont\nsetfont\n";

		String header_p1 = "%!PS\n/";

		/* typeset text: moveto x y -> from lower left in 72pts/inch increments */
		
		
		
		
		try {
    		//Add PS header to ouput buffer
	    	writeString(header_p1);
	    	writeString(header_p2);
	    	//enter text data
	    	int lineCount = 0;
    	    int curPtr = 0;
    	    int lineBreakPtr;
    	    int linePosition;
    	    String nextLine;

    	    while(curPtr < srcString.length())
    	    {
    	    	lineBreakPtr = srcString.indexOf("\n", curPtr);
    	    	if(((lineBreakPtr - curPtr) >= charsPerLineMax) || (lineBreakPtr == -1)) {
    				//No newline escape (\n) in this line
    				if((curPtr + charsPerLineMax) < srcString.length()){
    					//There's still enought text to create a full line
    					nextLine = srcString.substring(curPtr, curPtr + charsPerLineMax);
    				}
    				else {
    					//Else get the rest of srcString
    					nextLine = srcString.substring(curPtr);
    				}
    				curPtr += charsPerLineMax;
    	    	}
    	    	else {
    	    		//There's newline escape
    	    		nextLine = srcString.substring(curPtr, lineBreakPtr);
    	    		curPtr = lineBreakPtr+2;	//jump over the newline escape (\n)
    	    	}
    	    	linePosition = firstLinePos + (lineCount * kSingleSpacing);
    	    	lineStart(marginPoints, linePosition);
    	    	// replace paren with escaped paren and write to out		
    	    	writeString(nextLine.replace("(", "\\(").replace(")", "\\)"));
    	    	lineEnd();
    	        lineCount++;

    	        if(lineCount > linesPerPageMax)
    	        {
    	            lineCount = 0;
    	            showPage();
    	        }
    	    }

	    	Log.i(tag, out.toString());
    	}
    	catch (IOException e) {
    		Log.e(tag, "Error " + e);
    	}
    	return true;
	}

	/**
	 * Constructs a byte array and fills it with data that is read from the
	 * specified resource.
	 * @param filename the path to the resource
	 * @return the specified resource as a byte array
	 * @throws java.io.IOException if the resource cannot be read, or the
	 *   bytes cannot be written, or the streams cannot be closed
	 */
	private byte[] obtainByteData(String filename) throws IOException {
		Log.w(tag, "obtainByteData - filename: " + filename);
		File file = new File(filename);
	    InputStream inputStream = null;
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    byte[] byteData;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(file));
			// Read bytes from the input stream in bytes.length-sized chunks and
			// write
			// them into the output stream
			for (int readBytes = inputStream.read(); readBytes >= 0; readBytes = inputStream
					.read())
				outputStream.write(readBytes);

			// Convert the contents of the output stream into a byte array
			byteData = outputStream.toByteArray();
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		
	    outputStream.close();
	    Log.w("FormatPS", "obtainByteData - byteData.length = " + Integer.toString(byteData.length));
	    return byteData;
	}
	
	public static String encodeBytesToAscii85(byte[] bytes) {
	    ByteArrayOutputStream out_byte = new ByteArrayOutputStream();
	    ASCII85OutputStream  out_ascii = new ASCII85OutputStream(out_byte);

	    try {
	        out_ascii.write(bytes);
	        out_ascii.flush();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    String res = "";
	    try {
	        res = out_byte.toString("ascii");
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    }
	    return res;
	}


	
	public boolean formatPSForImage(PaperSize_C paperSize, int numberOfCopies, String path){
		ImageReader reader = new ImageReader(this.context, path);
		Bitmap srcBitmap = reader.getBitmapAndFree();
		File file = new File(path);
		String fileName = file.getName();
		
		int widthIn = srcBitmap.getWidth();
		int heightIn = srcBitmap.getHeight();

		Log.i(tag, "widthIn: "+widthIn);
		Log.i(tag, "heightIn: "+heightIn);
		Log.i(tag, "-fileName: "+fileName);
		
		boolean rotate = (widthIn > heightIn) ? true : false;
		rotate = false;
		
		String buff = null;
		byte[] byteArray;
		
		try {
//			byteArray = this.obtainByteData(Static.tempFolder + "/" + Static.webViewImage);
			byteArray = this.obtainByteData(path);
			Log.i(tag, "byteArray size: "+byteArray.length);
		}
		catch (IOException e) {
			Log.e("FormatPS", "obtainByteData Exception error: " + e.getMessage(),e);
			return false;
		}
		
		buff = encodeBytesToAscii85(byteArray);
		buff = buff.substring(0, buff.length() - 2);
		
		float maxWidth, maxHeight;
		if(rotate)
		{
			Log.i(tag, "rotate");
			maxWidth = (float) ((paperSize.inchHeight * inchInPoints) - marginWidth*2);
			maxHeight = (float) ((paperSize.inchWidth * inchInPoints) - marginHeight*2);
		}
		else
		{
			maxWidth = (float) ((paperSize.inchWidth * inchInPoints) - marginWidth*2);
			maxHeight = (float) ((paperSize.inchHeight * inchInPoints) - marginHeight*2);
		}
		
		float scaleFactorWidth = (float) 1.0;
		float scaleFactorHeight = (float) 1.0;

		// if it's too big at native resolution, downscale to fit
			scaleFactorWidth = maxWidth/(float)widthIn;

			scaleFactorHeight = maxHeight/(float)heightIn;
		// Use the downscaling factor for the side that requires the greatest downscale
		float scaleFactor = (scaleFactorWidth < scaleFactorHeight) ? scaleFactorWidth : scaleFactorHeight;
		
		Log.w("FormatPS", "scaleFactorHeight = " + Float.toString(scaleFactorHeight)
				+ "\nscaleFactorWidth = " + Float.toString(scaleFactorWidth)
				+ "\nscaleFactor = " + Float.toString(scaleFactor));

		float scaleWidth = (float)widthIn * scaleFactor;
		float scaleHeight = (float)heightIn * scaleFactor;

		// This centers the image vertically and horizontally
		int tx, ty;
		
		if(rotate)
		{	// from center of rotated sheet
			tx = (int) (((paperSize.inchHeight * inchInPoints) - scaleWidth)/2);
			ty = (int) (((paperSize.inchWidth * inchInPoints) - scaleHeight)/2);
		}
		else
		{	// from lower left
			ty = (int) (((paperSize.inchHeight * inchInPoints) - scaleHeight)/2);
			tx = (int) (((paperSize.inchWidth * inchInPoints) - scaleWidth)/2);
		}

		DateFormat dateFormat = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		
		Log.w("FormatPS", "maxWidth = " + Float.toString(maxWidth)
				+ ", \nmaxHeight = " + Float.toString(maxHeight)
				+ ", \ntx = " + Integer.toString(tx)
				+ ", \nty = " + Integer.toString(ty)
				+ ", \nwidthIn = " + Integer.toString(widthIn)
				+ ", \nheightIn = " + Integer.toString(heightIn)
				+ ", \nwidthOut = " + Float.toString(paperSize.inchWidth)
				+ ", \nscaleWidth = " + Float.toString(scaleWidth));

		String dscHeader = "%!PS-Adobe-3.0 EPSF-3.0\n%%Creator: PlusTechPrint\n%%Title: "
				+ fileName//"pstest.jpg" //IMG_0264.JPG
				+ "\n%%CreationDate: "+dateFormat.format(date)+"\n%%BoundingBox: "
				+ tx
				+ " "
				+ ty
				+ " "
				+ (int) scaleWidth
				+ ".000000 "
				+ (int) scaleHeight
				+ ".000000\n%%Pages: 1\n%%DocumentData: Clean7Bit\n%%LanguageLevel: 2\n%%EndComments\n"
				+ "%%BeginProlog\n%%EndProlog\n%%Page: 1 1\n";

		Log.w("FormatPS", dscHeader);
		
		String header = null;

		if (rotate)
			header = "90 rotate 0 -"
					+ (int) (paperSize.inchWidth * inchInPoints)
					+ " translate\n/RawData currentfile /ASCII85Decode filter def\n"
					+ "/Data RawData << >> /DCTDecode filter def\n" + tx + " "
					+ ty + " translate\n" + scaleWidth + " " + scaleHeight
					+ " scale\n"
					+ "/DeviceRGB setcolorspace\n{ << /ImageType 1\n";
		else
			header = "/RawData currentfile /ASCII85Decode filter def\n"
					+ "/Data RawData << >> /DCTDecode filter def\n" + tx + " "
					+ ty + " translate\n" + (int)scaleWidth + ".000000 " + (int)scaleHeight
					+ ".000000 scale\n"
					+ "/DeviceRGB setcolorspace\n{ << /ImageType 1\n";			

		String sizes = "/Width " + widthIn + "\n/Height " + heightIn
				+ "\n/ImageMatrix [ " + widthIn + " 0 0 -" + heightIn + " 0 "
				+ heightIn + " ]\n";
		String header_end = "/DataSource Data\n/BitsPerComponent 8\n"
				+ "/Decode [0 1 0 1 0 1]\n>> image\nData closefile\n"
				+ "RawData flushfile\nshowpage\n} exec\n";

		String footer = "\n~>\n%%EOF\n";
	
		
		try {
			writeString(dscHeader);
			writeString(header);			
			writeString(sizes);
			writeString(header_end);
			writeString(buff);
			writeString(footer);
		} catch (IOException e) {
			Log.e("FormatPS", this.getClass().getSimpleName() + " - FormatPSForImage - Fail to write to output stream - e = " + e.getMessage());
			return false;
		}
		return true;
	}
	
	private int encode(long tuple, int count, int pos, ByteArrayOutputStream out) throws IOException
	{
		int power85[] = { 1, 85, 85*85, 85*85*85, 85*85*85*85};
		long charOut;
		for (int i = 4; i >= 4-count; i--)
		{
			charOut = tuple / power85[i];
			out.write((int) (charOut + '!'));
			
			if ((pos++ >= 63) || ((pos == 1) && (charOut + '!' == '%'))) // don't want %% at start of line
			{
				pos = 0;
				out.write('\n');
			}

			tuple -= charOut * power85[i];
			
		}
		return pos;
	}
	
}
