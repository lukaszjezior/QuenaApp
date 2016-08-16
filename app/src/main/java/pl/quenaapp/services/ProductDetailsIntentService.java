package pl.quenaapp.services;

import pl.quenaapp.activities.ProductDetailsActivity;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

public class ProductDetailsIntentService extends IntentService {

	ConnectionService cm = new ConnectionService(this);
	Bitmap photo;
	private String TAG;

	public ProductDetailsIntentService() {
		super("ProductDetailsIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "uruchomiono metodê onHandleIntent");

		Bundle dataFromIntent = intent.getExtras();
		Log.i(TAG, dataFromIntent.getString("photoPath"));

		try {
			photo = cm.downloadImageByURL(dataFromIntent.getString("photoPath"));
			Log.i(TAG, "pobrano obazek");
		} catch (Exception e) {
			Log.i(TAG, "problem z internetem podczas pobierania obrazka");
		}
		Log.i(TAG, "po pobraniu obrazka");

		Log.i(TAG, "resize obrazka");
		// wystêpowa³ problem failed binder transaction, poniewa¿ bundle ma
		// ograniczony rozmiar
		
		Intent productDetailsIntent = new Intent(
				ProductDetailsActivity.ACTION_DOWNLOADED_IMAGE);
		Bundle productDetailsBundle = new Bundle(2);
		
		int h = 0;
		if(photo != null){
			float densityMultiplier = this.getResources().getDisplayMetrics().density;
			
			//w przypadku du¿ego rozmiaru zdjêcia, zmniejszamy go dwa razy bardziej
			if(photo.getWidth() > 1000 || photo.getHeight() > 1000){
				h = (int) (100 * densityMultiplier);
			} else {
				h = (int) (200 * densityMultiplier);
			}
			
			int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));
			productDetailsBundle.putParcelable("photo",
					Bitmap.createScaledBitmap(photo, w, h, true));
		} else {
			productDetailsBundle.putParcelable("photo", null);
		}
		
		productDetailsIntent.putExtras(productDetailsBundle);
		sendBroadcast(productDetailsIntent);

		Log.i(TAG, "intencja wys³ana na broadcast");
	}

}
