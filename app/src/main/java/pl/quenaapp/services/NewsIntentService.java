package pl.quenaapp.services;

import pl.quenaapp.activities.NewsActivity;
import pl.quenaapp.model.Product;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/*
 * Klasy IntentService'�w s� stworzone po to, aby podczas wczytywania danych, mo�na by�o obr�ci�
 * ekran bez wykrzaczenia aktywno�ci. 
 */

public class NewsIntentService extends IntentService {

	ConnectionService cm = new ConnectionService(this);
	ArrayList<Product> productList = null;

	private String TAG;

	public NewsIntentService() {
		super("NewsIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "uruchomiono metode onHandleIntent");

		try {
			productList = cm.getNews();
		} catch (Exception e) {
			Log.i(TAG, "problem z internetem podczas pobierania danych");
			productList = null;
		}

		Log.i(TAG, "po pobraniu nowosci");

		Intent newsIntent = new Intent(NewsActivity.ACTION_DOWNLOADED_NEWS);
		Bundle newsBundle = new Bundle(2);
		newsBundle.putParcelableArrayList("DOWNLOADED_CONTENT", productList);
		newsIntent.putExtras(newsBundle);
		sendBroadcast(newsIntent);
		Log.i(TAG, "intencja wyslana na broadcast");
		stopSelf();
	}

}
