package com.plustech.print.conversion;


import java.io.File;

import android.graphics.Bitmap;
import android.util.Log;

import com.cmc.osd.ndk.bitmap.Pix;
import com.cmc.osd.ndk.bitmap.ReadFile;
import com.cmc.osd.ndk.bitmap.Rotate;
import com.cmc.osd.ndk.bitmap.Scale;
import com.plustech.print.common.Common;
import com.plustech.print.common.PaperSizes.PaperSize_C;


/**
 * Conversion image to rasterize for PCL format.
 * 
 * @author NVan
 */
public class BitmapConversion {
	private static final String TAG = BitmapConversion.class.getSimpleName();
	private String file;
	private PaperSize_C paperSize;
	private boolean autoRotation;

	public BitmapConversion(String file, PaperSize_C paperSize,
			boolean autoRotation) {
		this.file = file;
		this.paperSize = paperSize;
		this.autoRotation = autoRotation;
	}

	/**
	 * Creates a Pix object from Bitmap data.
	 * 
	 * @return a Pix object
	 */
	public Pix readBitmap() {
		return ReadFile.readFile(new File(file));
	}

	public Pix conversionBitmapToPixForPCL() {
		try {
			Pix srcPix = readBitmap();
			Pix grayscaledPixaa = Scale.scaleRGBToGrayFast1(srcPix, 1, 0.3f,
					0.59f, 0.11f);
			int pixelsWide = srcPix.getWidth();
			int pixelsHigh = srcPix.getHeight();
			Log.i(TAG, "Org width is " + pixelsWide + ", height is "
					+ pixelsHigh);
			Log.i(TAG, "Paper size is " + paperSize.name);

			// Check rotation
			Pix rotatedPix, scaledPix;
			if (autoRotation)
				// 0. Rotate if needed
				if (pixelsWide > pixelsHigh) {
					Log.i("FormatPCLForImage", "Rotating...");
					rotatedPix = Rotate.rotate90(srcPix, -1);
				} else
					rotatedPix = srcPix.clone();
			else
				rotatedPix = srcPix.clone();
			srcPix.recycle();

			pixelsWide = rotatedPix.getWidth();
			pixelsHigh = rotatedPix.getHeight();
			Log.i(TAG, "After autorotate: width is " + pixelsWide
					+ " height is " + pixelsHigh);

			float dstWidth = paperSize.inchWidth;
			float dstHeight = paperSize.inchHeight;
			Log.i(TAG, "Paper size: width is " + dstWidth + " height is "
					+ dstHeight);

			int imageLongestAxisLength = pixelsWide;
			if (pixelsHigh > imageLongestAxisLength)
				imageLongestAxisLength = pixelsHigh;

			float outAreaLongestAxisLength = dstWidth;
			if (dstHeight > outAreaLongestAxisLength)
				outAreaLongestAxisLength = dstHeight;

			float WidthMargin = (float) 0.75;

			float scaleFactorNeeded = 1f;

			// Resolution PCL_DPI_300 or PCL_DPI_150? -> need check crash
			// the scaling factor is a half as expected and apply a 2x
			// monochrome upscaling later, see below
			// 1. Scaling
			if (autoRotation) {
				float desiredWidth = ((float) outAreaLongestAxisLength - WidthMargin)
						* (float) Common.PCL_RESOLUTIONS[Common.PCL_DPI_150];
				scaleFactorNeeded = desiredWidth
						/ (float) imageLongestAxisLength;

			} else {
				float scaleX = (dstWidth - WidthMargin)
						* (float) Common.PCL_RESOLUTIONS[Common.PCL_DPI_150]
						/ (float) pixelsWide;
				float scaleY = (dstHeight - WidthMargin)
						* (float) Common.PCL_RESOLUTIONS[Common.PCL_DPI_150]
						/ (float) pixelsHigh;
				scaleFactorNeeded = Math.min(scaleX, scaleY);
				Log.i(TAG, "scaleX = " + scaleX + "; scaleY = " + scaleY
						+ "; scaleFactor = " + scaleFactorNeeded);
			}

			scaledPix = Scale.scale(rotatedPix, scaleFactorNeeded);
			rotatedPix.recycle();
			// 2. Gray downscaling
			Pix grayscaledPix = Scale.scaleRGBToGrayFast1(scaledPix, 1, 0.3f,
					0.59f, 0.11f);
			scaledPix.recycle();
			pixelsWide = grayscaledPix.getWidth();
			pixelsHigh = grayscaledPix.getHeight();
			Log.i(TAG, "After size scaling and gray downscaling: width is "
					+ pixelsWide + "; height is " + pixelsHigh);
			// 3. Binary upscaling (2x expansion) --> the resulting monochrome
			// resolution - 600, see below
			Pix binaryscaledPix = Scale.scaleGray2xLIDither(grayscaledPix);
			grayscaledPix.recycle();
			pixelsWide = binaryscaledPix.getWidth();
			pixelsHigh = binaryscaledPix.getHeight();
			Log.i(TAG, "After binary 2x upscaling: width is " + pixelsWide
					+ " height is " + pixelsHigh);

//			this.bmp.recycle();

			return binaryscaledPix;
		} catch (Exception ex) {
			Log.e(TAG, ex.getMessage(), ex);
			return null;
		}
	}
}
