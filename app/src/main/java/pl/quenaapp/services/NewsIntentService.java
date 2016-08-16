package pl.quenaapp.services;

import pl.quenaapp.activities.NewsActivity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/*
 * Klasy IntentService'ów s¹ stworzone po to, aby podczas wczytywania danych, mo¿na by³o obróciæ
 * ekran bez wykrzaczenia aktywnoœci. 
 */

public class NewsIntentService extends IntentService {

	ConnectionService cm = new ConnectionService(this);
	String[] newsTab = null;

	private String TAG;

	public NewsIntentService() {
		super("NewsIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "uruchomiono metodê onHandleIntent");

		try {
			newsTab = cm.getNews();
		} catch (Exception e) {
			Log.i(TAG, "problem z internetem podczas pobierania danych");
			newsTab = null;
		}

		Log.i(TAG, "po pobraniu nowoœci");

		Intent newsIntent = new Intent(NewsActivity.ACTION_DOWNLOADED_NEWS);
		Bundle newsBundle = new Bundle(2);
		newsBundle.putStringArray("DOWNLOADED_CONTENT", newsTab);
		newsIntent.putExtras(newsBundle);
		sendBroadcast(newsIntent);
		Log.i(TAG, "intencja wys³ana na broadcast");
		stopSelf();
	}

}
