package com.plustech.print.drivers.format;

import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.cmc.osd.ndk.bitmap.Pix;
import com.plustech.print.common.Common;
import com.plustech.print.common.PaperSizes.PaperSize_C;
import com.plustech.print.conversion.BitmapConversion;
import com.plustech.print.reader.ImageReader;

/**
 * Format data for PCL from an image or text.
 * 
 * @author NVan
 */
public class FormatPCL {
	private static final String TAG = FormatPCL.class.getSimpleName();
	private Context context;
	private static final String US_ASCII = "US-ASCII";

	private static final String ISO_8859_1 = "ISO-8859-1";

	private OutputStream out;

	private static final int inchInPoints = 72;
	private static final int marginPoints = 40;

	private static final int kCourier10CharPerInch = 12;
	// private static final int kCourier12CharPerInch = 10;

	private static final int kSingleSpacing = 20;
	private static final int kPCLSpacingForCalculation = 16;

	public FormatPCL(Context context, OutputStream out) {
		this.out = out;
		this.context = context;
	}

	/**
	 * Main constructor.
	 * 
	 * @param out
	 *            the OutputStream to write the PCL stream to
	 * @param maxResolution
	 *            the maximum resolution to encode bitmap images at
	 */
	public FormatPCL(Context context, OutputStream out, int maxResolution) {
		this(context, out);
		boolean found = false;
		int i;
		for (i = 0; i < Common.PCL_RESOLUTIONS.length; i++) {
			if (Common.PCL_RESOLUTIONS[i] == maxResolution) {
				found = true;
				break;
			}
		}
		if (found) {
		}
	}

	/**
	 * Writes a PCL escape command to the output stream.
	 * 
	 * @param cmd
	 *            the command (without the ESCAPE character)
	 * @throws IOException
	 *             In case of an I/O error
	 */
	private void writeCommand(String cmd) throws IOException {
		out.write(27); // ESC
		out.write(cmd.getBytes(US_ASCII));
	}

	/**
	 * Writes raw text (in ISO-8859-1 encoding) to the output stream.
	 * 
	 * @param s
	 *            the text
	 * @throws IOException
	 *             In case of an I/O error
	 */
	private void writeText(String s) throws IOException {
		out.write(s.getBytes(ISO_8859_1));
	}

	/**
	 * Sends the universal end of language command (UEL).
	 * 
	 * @throws IOException
	 *             In case of an I/O error
	 */
	private void universalEndOfLanguage() throws IOException {
		writeCommand("%-12345X");
	}

	// setOrientation "\x1b&l%dO"
	private void setOrientation(int rotate) throws IOException {
		writeCommand("&l" + rotate + "O");
	}

	// setPageLayout "\x1b*r0F"
	private void setPageLayout() throws IOException {
		writeCommand("*r0F");
	}

	/**
	 * Resets the printer and restores the user default environment.
	 * 
	 * @throws IOException
	 *             In case of an I/O error
	 */
	private void resetPrinter() throws IOException {
		writeCommand("E");
	}

	/**
	 * Sends the job separation command.
	 * 
	 * @throws IOException
	 *             In case of an I/O error
	 */
	private void separateJobs() throws IOException {
		writeCommand("&l1T");
	}

	/**
	 * Sends the form feed character.
	 * 
	 * @throws IOException
	 *             In case of an I/O error
	 */
	private void formFeed() throws IOException {
		out.write(12); // =OC ("FF", Form feed)
	}

	/**
	 * Sets the raster graphics resolution
	 * 
	 * @param value
	 *            the resolution value (units per inch)
	 * @throws IOException
	 *             In case of an I/O error
	 */
	private void setRasterGraphicsResolution(int value) throws IOException {
		writeCommand("*t" + value + "R");
	}

	/**
	 * Sets the number of copies.
	 * 
	 * @param number
	 *            of copies
	 * @throws IOException
	 *             In case of an I/O error
	 */
	private void setNumberOfCopies(int numberOfCopies) throws IOException {
		Log.i("FormatPCL - setNumberofcopies", "" + numberOfCopies);
		writeCommand("&l" + numberOfCopies + "X");
	}

	/**
	 * Set PCL Height.
	 * 
	 * @param pcl
	 *            height
	 * @throws IOException
	 */
	private void setPCLHeight(int pclHeight) throws IOException {
		writeCommand("*r" + pclHeight + "T");
	}

	/**
	 * Set PCL Width.
	 * 
	 * @param pcl
	 *            Width
	 * @throws IOException
	 */
	private void setPCLWidth(int pclWidth) throws IOException {
		writeCommand("*r" + pclWidth + "S");
	}

	/**
	 * Raster Start.
	 * 
	 * @param useCurrentPosition
	 *            true if using current cursor position to draw raster image
	 *            false if using left most position
	 * @throws IOException
	 */
	private void rasterStart(boolean useCurrentPosition) throws IOException {
		if (useCurrentPosition) {
			writeCommand("*r1A");
		} else {
			writeCommand("*r0A");
		}
	}

	/**
	 * Raster Stop.
	 * 
	 * @throws IOException
	 */
	private void rasterStop() throws IOException {
		writeCommand("*rC");
	}

	/**
	 * Setup Font.
	 * 
	 * @throws IOException
	 */
	private void fontSetup() throws IOException {
		writeCommand("&k3G");
		writeCommand("(s12H");
		writeCommand("(s4099T");
		writeCommand(")s4099T");
	}

	/**
	 * Setup page unit.
	 * 
	 * @throws IOException
	 */
	private void pageUnitsSetup() throws IOException {
		writeCommand("&u" + inchInPoints + "D");
	}

	/**
	 * Start line.
	 * 
	 * @throws IOException
	 */
	private void lineStart(int x, int y) throws IOException {
		writeCommand("*p" + x + "X");
		writeCommand("*p" + y + "Y");
	}

	/**
	 * Format data for PCL from an Image
	 * 
	 * @param fileName
	 * @param paperSize
	 * @param numberOfCopies
	 * @param autoRotation
	 * @return
	 */
	public boolean formatPCLForImage(String fileName, PaperSize_C paperSize,
			int numberOfCopies, boolean autoRotation) {
		boolean res = false;
		ImageReader reader = new ImageReader(this.context, fileName);
		Bitmap bitmap = reader.getBitmapAndFree();
		if(bitmap == null)
			Log.i(TAG, "bitmap is null");
		
		BitmapConversion convertion = new BitmapConversion(fileName, paperSize,
				autoRotation);
		Pix binaryPix = convertion.conversionBitmapToPixForPCL();
//		bitmap.recycle();

		int pixelsWide = binaryPix.getWidth();
		int pixelsHigh = binaryPix.getHeight();
		Log.i(TAG, "After binary 2x upscaling: width is " + pixelsWide
				+ " height is " + pixelsHigh);

		// Add PCL header to ouput buffer
		try {
			universalEndOfLanguage();
			resetPrinter();
			// setCursor();
			setNumberOfCopies(numberOfCopies);
			setRasterGraphicsResolution(Common.PCL_RESOLUTIONS[Common.PCL_DPI_300]);
			setOrientation(0);
			setPCLHeight(pixelsHigh);
			int wpl = (pixelsWide + 31) / 32;
			int paddedPixels = wpl * 4 * 8 - pixelsWide;
			setPCLWidth(pixelsWide + paddedPixels);
			setPageLayout();
			rasterStart(false);
			for (int y = 0; y < pixelsHigh; y++) {
				Log.i("", "build rasterize line: "+y);
				byte rasterLine[] = binaryPix.getLineData(y);
				writeCommand("*b" + Integer.toString(rasterLine.length) + "W");
				out.write(rasterLine);
				Log.i("", "sended rasterize line: "+y);
			}
			
			Log.i("", "rasterStop... ");
			rasterStop();
			Log.i("", "separateJobs... ");
			separateJobs();
			Log.i("", "resetPrinter... ");
			resetPrinter();
			Log.i("", "universalEndOfLanguage... ");
			universalEndOfLanguage();
			Log.i("", "recycle... ");
			binaryPix.recycle();
			Log.i("", "flush");
			// out.flush();
			// srcBitmap.recycle();
		} catch (IOException e) {
			e.printStackTrace();
			return res;
		}
		return true;
	}

	/**
	 * Format data for PCL from text
	 * 
	 * @param srcString
	 * @param paperSize
	 * @param numberOfCopies
	 * @return
	 */
	public boolean formatPCLForText(String srcString, PaperSize_C paperSize,
			int numberOfCopies) {
		float widthOut = paperSize.inchWidth;
		float heightOut = paperSize.inchHeight;
		int charsPerLineMax = (int) ((widthOut - 1) * kCourier10CharPerInch);
		int firstLinePos = marginPoints;
		int linesPerPageMax = (int) (((heightOut * inchInPoints) - (marginPoints * 2)) / kPCLSpacingForCalculation);

		try {
			// Add PCL header to ouput buffer
			universalEndOfLanguage();
			resetPrinter();
			setNumberOfCopies(numberOfCopies);
			fontSetup();
			pageUnitsSetup();
			// enter text data
			int lineCount = 0;
			int curPtr = 0;
			int lineBreakPtr;
			int linePosition;
			String nextLine;

			while (curPtr < srcString.length()) {
				lineBreakPtr = srcString.indexOf("\n", curPtr);
				if (((lineBreakPtr - curPtr) >= charsPerLineMax)
						|| (lineBreakPtr == -1)) {
					// No newline escape (\n) in this line
					if ((curPtr + charsPerLineMax) < srcString.length()) {
						// There's still enought text to create a full line
						nextLine = srcString.substring(curPtr, curPtr
								+ charsPerLineMax);
					} else {
						// Else get the rest of srcString
						nextLine = srcString.substring(curPtr);
					}
					curPtr += charsPerLineMax;
				} else {
					// There's newline escape
					nextLine = srcString.substring(curPtr, lineBreakPtr);
					curPtr = lineBreakPtr + 2; // jump over the newline escape
												// (\n)
				}
				linePosition = firstLinePos + (lineCount * kSingleSpacing);
				lineStart(marginPoints, linePosition);
				writeText(nextLine);

				lineCount++;

				if (lineCount > linesPerPageMax) {
					lineCount = 0;
					formFeed();
				}
			}

			formFeed();
			// Add PCL footer
			separateJobs();
			resetPrinter();
			universalEndOfLanguage();
			// out.flush();
			Log.i(TAG, out.toString());
		} catch (IOException e) {
			Log.e(TAG, "Error " + e);
		}
		return true;
	}
}
