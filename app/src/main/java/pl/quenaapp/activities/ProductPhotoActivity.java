package pl.quenaapp.activities;

import pl.quenaapp.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class ProductPhotoActivity extends Activity{

	public static final String zdjecieSciezka = "";
	private WebView wv;
	private String pathToPhoto;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_photo);
		wv = (WebView) findViewById(R.id.photoWebView);
		
		Intent photoIntent = getIntent();
		Bundle dataIntent = photoIntent.getExtras();
		pathToPhoto = dataIntent.getString("zdjecieSciezka");
		
		if (savedInstanceState != null)
			wv.restoreState(savedInstanceState);
		else
			wv.loadUrl(pathToPhoto);
		
		wv.getSettings().setBuiltInZoomControls(true);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		wv.saveState(outState);
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
}
