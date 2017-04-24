package com.plustech.print.congdongandroid;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.plustech.print.BaseActivity;
import com.plustech.print.R;
import com.plustech.print.common.Common;

public class WebActivity extends BaseActivity implements OnClickListener {

	private EditText webUrl;
	private Button goButton;
	private WebView webView;

	private Button nextprint;
	private Button backhome;

	private int validategoweb = 0;
	File sdCard = Environment.getExternalStorageDirectory();
	File dir = new File(sdCard.getAbsolutePath());
	File fileimage = new File(dir, "filename.jpg");

	ProgressBar progressBar;
	private static final String tag = WebActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webpage_home);

		nextprint = (Button) findViewById(R.id.webpage_next);
		webUrl = (EditText) findViewById(R.id.addressbar);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility(View.INVISIBLE);
		webView = (WebView) findViewById(R.id.webview);
		webView.setWebViewClient(new Webpage());
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				// Make the bar disappear after URL is loaded, and changes
				// string to Loading...
				// Make the bar disappear after URL is loaded
				System.out.println("Value of progress" + progress);
				progressBar.setProgress(progress);
				progressBar.setVisibility(View.VISIBLE);

				if (progress == 100)
					nextprint.setOnClickListener(WebActivity.this);
				progressBar.setVisibility(View.INVISIBLE);
			}
		});
		goButton = (Button) findViewById(R.id.button_go);
		goButton.setOnClickListener(new OnClickListener() {

			@SuppressLint("SetJavaScriptEnabled")
			@Override
			public void onClick(View view) {

				String url = webUrl.getText().toString();
				if (CheckConnecActivity.checkNow(getBaseContext())) {
					if (url.length() > 0) {
						if (validateUrl(url) == false) {
							url = "http://" + url;
						}
						try {
							webView.getSettings().setJavaScriptEnabled(true);
							webView.loadUrl(url);
							validategoweb = 1;

							Log.d(tag, "load url" + url);
						} catch (NullPointerException e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					} else {
						// Toast.makeText(getBaseContext(),
						// getString(R.string.nulltextview), 1).show();

						webUrl.requestFocus();

					}
				} else {
					url = webUrl.getText().toString();
					if (url.length() > 0) {
						notnetwork();
					}
				}

			}

			private boolean validateUrl(String url) {
				return url.contains("http://");
			}
		});

		backhome = (Button) findViewById(R.id.button_back_webhome);
		backhome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

	}

	public boolean isConnected(String url) {
		try {
			HttpGet request = new HttpGet(url);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
				@Override
				public long getKeepAliveDuration(HttpResponse response,
						HttpContext context) {
					return 0;
				}
			});
			HttpResponse response = httpClient.execute(request);
			return response.getStatusLine().getStatusCode() == 200;

		} catch (IOException e) {
		}
		return false;
	}

	protected void notnetwork() {

		final Dialog dialog = new Dialog(WebActivity.this);
		dialog.setContentView(R.layout.dialog_notwebview);
		TextView tv = (TextView) dialog.findViewById(R.id.tv1notwebview);
		tv.setText("Not NetWork");
		Button btcancel = (Button) dialog.findViewById(R.id.cancel_print);
		btcancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();

	}

	public class Webpage extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			view.loadUrl(url);
			return true;
		}

		public void onPageFinished(WebView view, String url) {
			progressBar.setVisibility(View.GONE);
			WebActivity.this.progressBar.setProgress(100);
			Picture picture = view.capturePicture();
			if (picture.getWidth() > 0 && picture.getHeight() > 0) {

				Bitmap image = Bitmap.createBitmap(picture.getWidth(),picture.getHeight(), Config.ARGB_8888);
				Canvas canvas = new Canvas(image);
				picture.draw(canvas);
				if (image != null) {
					ByteArrayOutputStream mByteArrayOS = new ByteArrayOutputStream();
					image.compress(Bitmap.CompressFormat.JPEG, 90, mByteArrayOS);
					try {
						File sdCard = Environment.getExternalStorageDirectory();
						File dir = new File(sdCard.getAbsolutePath());
						File file = new File(dir, "filename.jpg");
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(mByteArrayOS.toByteArray());

						fos.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();

					}
				}
			}
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			progressBar.setVisibility(View.VISIBLE);
			WebActivity.this.progressBar.setProgress(0);
			super.onPageStarted(view, url, favicon);
		}
	}

	public void setValue(int progress) {
		this.progressBar.setProgress(progress);
	}

	@Override
	public void onClick(View v) {
		if (validategoweb == 1) {
			Intent intent =  Common.buildIntent(fileimage.getPath());
			setResult(RESULT_OK, intent);
			finish();
			Log.d("imageWeb", "" + fileimage.getPath());

		}

	}

}