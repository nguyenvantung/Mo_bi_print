package com.cmc.osd.ndk.bitmap;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class BitmapHolder {
	public ByteBuffer _handler = null;
	public Context mContext;
	static String tag = BitmapHolder.class.getSimpleName();

	// NDK STUFF
	static {
		System.loadLibrary("bmholder");
	}

	/**
	 * Store bitmap
	 * 
	 * @param bitmap
	 * @return
	 */
	private native ByteBuffer storeBitmapData(Bitmap bitmap);

	/**
	 * Get bitmap from store
	 * 
	 * @param handler
	 * @return
	 */
	private native Bitmap getBitmapFromStored(ByteBuffer handler);

	/**
	 * Free bitmap
	 * 
	 * @param handler
	 */
	private native void freeBitmapData(ByteBuffer handler);

	/**
	 * Rotate bimap
	 * 
	 * @param handler
	 */
	private native void rotateBitmapCcw90(ByteBuffer handler);

	/**
	 * Crop bitmap
	 * 
	 * @param handler
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	private native void cropBitmap(ByteBuffer handler, final int left,
			final int top, final int right, final int bottom);

	/**
	 * Convert a bitmap to gray
	 * 
	 * @param bitmapIn
	 * @param bitmapOut
	 */
	public native void convertToGray(Bitmap bitmapIn, Bitmap bitmapOut);

	public native void changeBrightness(int direction, Bitmap bitmap);

	public native void findEdges(Bitmap bitmapIn, Bitmap bitmapOut);

	// END NDK STUFF

	public BitmapHolder(Context mContext) {
		this.mContext = mContext;
	}

	public BitmapHolder(Context mContext, String fileName) {
		this.mContext = mContext;
		File file = new File(fileName);
		if(file.exists())
			Log.i("BitmapHolder", "fileName: " + fileName);
//		Pix pix = ReadFile.readFile(file);
//		if(pix == null)
//			Log.i("BitmapHolder", "Pix is null");
//		Bitmap bitmap = convertPixToBitmap(pix);
		Bitmap bitmap = decodeLargeFiles(this.mContext, fileName);
		Log.i("BitmapHolder", "storeBitmap");
		storeBitmap(bitmap);
	}

	public BitmapHolder(Context mContext, final Bitmap bitmap) {
		this.mContext = mContext;
		storeBitmap(bitmap);
	}

	public Bitmap convertPixToBitmap(Pix pix) {
		try {
			Pix temp = pix.copy();
			byte[] bitmapdata = temp.getData();
			Log.i("BitmapHolder", "bitmapdata: "+bitmapdata.length);
			Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0,
					bitmapdata.length);
			return bitmap;
		} catch (Exception ex) {
			Log.e("BitmapHolder", ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * Loading large image/bitmap files
	 * 
	 * @param fileName
	 * @param mContext
	 * @return
	 */
	public Bitmap decodeLargeFiles(Context mContext, String fileName) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(fileName, options);

		long totalImagePixes = options.outHeight * options.outWidth;

		// Detect maximum number of pixels supported;
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);

		long totalScreenPixels = metrics.heightPixels * metrics.widthPixels;

		if (totalImagePixes > totalScreenPixels) {
			double factor = (float) totalImagePixes	/ (float) (totalScreenPixels);
			int sampleSize = (int) Math.pow(2, Math.floor(Math.sqrt(factor)));
			options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
			options.inSampleSize = sampleSize;
			return BitmapFactory.decodeFile(fileName, options);
		}

		return BitmapFactory.decodeFile(fileName);
	}

	/**
	 * Loading large image/bitmap files
	 * 
	 * @param id
	 *            : resource id
	 * @param mContext
	 * @return
	 */
	public Bitmap decodeResourceLargeFiles(Context mContext, int id) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeResource(mContext.getResources(), id, options);

		long totalImagePixes = options.outHeight * options.outWidth;

		// Detect maximum number of pixels supported;
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);

		long totalScreenPixels = metrics.heightPixels * metrics.widthPixels;

		Log.i(tag, "Total Screen Pixels: " + totalScreenPixels);
		Log.i(tag, "Total Image Pixes: " + totalImagePixes);

		if (totalImagePixes > totalScreenPixels) {
			double factor = (float) totalImagePixes
					/ (float) (totalScreenPixels);
			int sampleSize = (int) Math.pow(2, Math.floor(Math.sqrt(factor)));
			options.inJustDecodeBounds = false;
			options.inSampleSize = sampleSize;
			return BitmapFactory.decodeResource(mContext.getResources(), id,
					options);
		}

		// load bitmap from resources
		options = new BitmapFactory.Options();
		// Make sure it is 24 bit color as our image processing algorithm
		// expects this format
		options.inPreferredConfig = Config.ARGB_8888;
		return BitmapFactory.decodeResource(mContext.getResources(), id,
				options);
	}

	public void storeBitmap(final Bitmap bitmap) {
		Log.i(tag, "_handler: ");
		if (_handler != null)
			freeBitmap();
		
		Log.i(tag, "_handler: storeBitmapData");
		_handler = storeBitmapData(bitmap);
		
	}

	public void rotateBitmapCcw90() {
		if (_handler == null)
			return;
		rotateBitmapCcw90(_handler);
	}

	public void cropBitmap(final int left, final int top, final int right,
			final int bottom) {
		if (_handler == null)
			return;
		cropBitmap(_handler, left, top, right, bottom);
	}

	public Bitmap getBitmap() {
		if (_handler == null)
			return null;
		return getBitmapFromStored(_handler);
	}

	public Bitmap getBitmapAndFree() {
		final Bitmap bitmap = getBitmap();
		freeBitmap();
		return bitmap;
	}

	public void freeBitmap() {
		if (_handler == null)
			return;
		freeBitmapData(_handler);
		_handler = null;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (_handler == null)
			return;
		Log.w("DEBUG",
				"JNI bitmap wasn't freed nicely.please rememeber to free the bitmap as soon as you can");
		freeBitmap();
	}
}