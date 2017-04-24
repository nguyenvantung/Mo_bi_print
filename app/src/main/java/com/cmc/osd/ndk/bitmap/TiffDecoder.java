/* libtiffdecoder A tiff decoder run on android system. Copyright (C) 2009 figofuture
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later 
 * version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 * 
 * */

package com.cmc.osd.ndk.bitmap;


import android.graphics.Bitmap;


public class TiffDecoder {

	/**
	 * Init decoder
	 * @param name: Path to tiff file
	 * @return
	 */
	public static native int nativeTiffOpen(String name);

	/**
	 * Convert tiff file to Pixels array
	 * @return
	 */
	public static native int[] nativeTiffGetBytes();

	/**
	 * Length of tiff
	 * @return
	 */
	public static native int nativeTiffGetLength();

	/**
	 * With of tiff
	 * @return
	 */
	public static native int nativeTiffGetWidth();

	/**
	 * Height of tiff
	 * @return
	 */
	public static native int nativeTiffGetHeight();

	/**
	 * Close tiff file after readed
	 */
	public static native void nativeTiffClose();

	/**
	 * Get bitmap from tiff file
	 * @param isTiffClose: True for close Tiff
	 * @return Bitmap object with ARB_8888
	 */
	public static Bitmap getBitmap(boolean isTiffClose) {
		int[] pixels = nativeTiffGetBytes();
		Bitmap mBitmap = Bitmap.createBitmap(pixels, TiffDecoder.nativeTiffGetWidth(),
				TiffDecoder.nativeTiffGetHeight(), Bitmap.Config.ARGB_8888);
		if(isTiffClose)
			nativeTiffClose();
		return mBitmap;
	}

	/**
	 * Get bitmap from tiff file with custom Bitmap Config
	 * @param isTiffClose: True for close Tiff
	 * @param config: Bitmap config
	 * @return Bitmap Object
	 */
	public static Bitmap getBitmap(boolean isTiffClose, Bitmap.Config config) {
		int[] pixels = nativeTiffGetBytes();
		Bitmap mBitmap = Bitmap.createBitmap(pixels, TiffDecoder.nativeTiffGetWidth(),
				TiffDecoder.nativeTiffGetHeight(), config);
		if(isTiffClose)
			nativeTiffClose();
		return mBitmap;
	}
	
	static {
		System.loadLibrary("tiff");
		System.loadLibrary("tiffdecoder");
	}
}
