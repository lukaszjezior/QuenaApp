package pl.quenaapp.services;

import pl.quenaapp.activities.CategoryActivity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class CategoryIntentService extends IntentService {
	private ConnectionService cm = new ConnectionService(this);
	private String[] categoriesTab = null;

	private String TAG;

	public CategoryIntentService() {
		super("CategoryIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "uruchomiono metodê onHandleIntent");
		Bundle daneZIntencji = intent.getExtras();

		try {
			categoriesTab = cm.getCategoriesByParentNumber(daneZIntencji
					.getInt("parentNumber"));
		} catch (Exception e) {
			Log.i(TAG, "problem z internetem podczas pobierania danych");
			categoriesTab = null;
		}

		Log.i(TAG, "po pobraniu kategorii");

		Intent categoryIntent = new Intent(
				CategoryActivity.ACTION_DOWNLOADED_CATEGORY);
		Bundle categoryBundle = new Bundle(2);
		categoryBundle.putStringArray("DOWNLOADED_CONTENT", categoriesTab);
		categoryIntent.putExtras(categoryBundle);
		sendBroadcast(categoryIntent);
		Log.i(TAG, "intencja wys³ana na broadcast");
		stopSelf();
	}

}
