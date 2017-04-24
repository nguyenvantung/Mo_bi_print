package com.plustech.print.reader;

import java.io.File;

import com.cmc.osd.ndk.bitmap.BitmapHolder;
import com.cmc.osd.ndk.bitmap.Pix;
import com.cmc.osd.ndk.bitmap.ReadFile;
import com.cmc.osd.ndk.bitmap.TiffDecoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageReader {
	private Context context;
	BitmapHolder holder;
	private String filePath;

	public enum ImageType {
		JPG(1), TIFF(3), OTHER(4);
		private int value;

		private ImageType(int value) {
			this.setValue(value);
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	};

	public ImageReader(Context context, String filePath) {
		this.context = context;
		holder = new BitmapHolder(this.context, filePath);
		this.filePath = filePath;
	}

	public ImageReader(Context context) {
		this.context = context;
		holder = new BitmapHolder(this.context);
	}

	/**
	 * Store a bitmap
	 * @param bitmap
	 */
	public void storeBitmap(Bitmap bitmap) {
		holder.storeBitmap(bitmap);
	}

	public void freeBitmap() {
		holder.freeBitmap();
	}

	public Bitmap getBitmapAndFree() {
//		if(holder._handler != null)
			return holder.getBitmapAndFree();
//		else
//			return null;
	}

	public Bitmap getBitmap() {
		File file = new File(filePath);
		if (!file.exists())
			return null;
		ImageType imgType = getFormat(filePath);
		Bitmap bitmap = null;
		switch (imgType) {
		case JPG:
			Pix srcPix = ReadFile.readFile(file);
			bitmap = convertPixToBitmap(srcPix);
			srcPix.recycle();
			storeBitmap(bitmap);
			break;
		case TIFF:
			TiffDecoder.nativeTiffOpen(file.getAbsolutePath());
			bitmap = TiffDecoder.getBitmap(true);
			break;
		case OTHER:
			bitmap = holder.decodeLargeFiles(this.context, filePath);
			storeBitmap(bitmap);
			break;
		default:
			break;
		}
		return bitmap;
	}

	private Bitmap convertPixToBitmap(Pix pix) {
		Pix temp = pix.copy();
		byte[] bitmapdata = temp.getData();
		Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0,
				bitmapdata.length);
		temp.recycle();
		return bitmap;
	}

	public static ImageType getFormat(String imageName) {
		String temp = new String(imageName);
		temp.toLowerCase();
		if (temp.toLowerCase().endsWith(".jpg")
				|| temp.toLowerCase().endsWith(".jpeg"))
			return ImageType.JPG;

		if (temp.toLowerCase().endsWith(".tif")
				|| temp.toLowerCase().endsWith(".tiff"))
			return ImageType.TIFF;

		return ImageType.OTHER;

	}
}
