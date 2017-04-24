package com.plustech.print.congdongandroid;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.cmc.osd.ndk.bitmap.BitmapHolder;
import com.imagezoom.ImageAttacher;
import com.imagezoom.ImageAttacher.OnMatrixChangedListener;
import com.imagezoom.ImageAttacher.OnPhotoTapListener;
import com.plustech.print.PrintApplication;
import com.plustech.print.R;

public class PrintPreviewFull extends Activity {
	ImageView imageView;
	private File path;
	Activity activity;

	public Button button_back;
	Bundle bundle;
	public Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_print_preview_fullscreen);
		activity = new Activity();

		bundle = getIntent().getExtras();
		path = new File(bundle.getString("image_position"));
		Log.d("path", "path" + path);

		imageView = (ImageView) findViewById(R.id.print_preview_fullscreen);
		BitmapHolder holder = new BitmapHolder(PrintApplication.getAppContext());
		bitmap = holder.decodeLargeFiles(getBaseContext(), path.getAbsolutePath().toString());
		imageView.setImageBitmap(bitmap);
		/**
		 * Use Simple ImageView
		 */
		usingSimpleImage(imageView);

		button_back = (Button) findViewById(R.id.back_full_image);
		button_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
	}


	public void usingSimpleImage(ImageView imageView) {
		ImageAttacher mAttacher = new ImageAttacher(imageView);
		ImageAttacher.MAX_ZOOM = 2.0f; // Double the current Size
		ImageAttacher.MIN_ZOOM = 0.5f; // Half the current Size
		MatrixChangeListener mMaListener = new MatrixChangeListener();
		mAttacher.setOnMatrixChangeListener(mMaListener);
		PhotoTapListener mPhotoTap = new PhotoTapListener();
		mAttacher.setOnPhotoTapListener(mPhotoTap);
	}

	private class PhotoTapListener implements OnPhotoTapListener {

		@Override
		public void onPhotoTap(View view, float x, float y) {
		}
	}

	private class MatrixChangeListener implements OnMatrixChangedListener {

		@Override
		public void onMatrixChanged(RectF rect) {

		}
	}
	
	

}
