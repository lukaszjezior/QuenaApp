package pl.quenaapp.activities;

import pl.quenaapp.R;
import pl.quenaapp.services.ConnectionService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

@SuppressLint("NewApi")
public class ProductDescriptionActivity extends Activity {
	
	public static final String produktId = "";
	private ConnectionService connectionService = new ConnectionService(this);
	private WebView wv;

	String productId;
	String url = connectionService.getSerwer() + "android/description.php?productId=";
	private String TAG;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_description_max);
		wv = (WebView) findViewById(R.id.longDescriptionWebView);
		
		Intent descriptionIntent = getIntent();
		Bundle intentData = descriptionIntent.getExtras();
		productId = intentData.getString("produktId");
		Log.i(TAG, "numerid=" + productId);
		
		
		if (savedInstanceState != null)
			wv.restoreState(savedInstanceState);
		else
			wv.loadUrl(url + productId);
		
		wv.getSettings().setBuiltInZoomControls(true);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		wv.saveState(outState);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
}
