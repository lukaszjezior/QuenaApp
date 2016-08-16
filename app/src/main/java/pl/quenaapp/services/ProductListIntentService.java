package pl.quenaapp.services;

import pl.quenaapp.activities.ProductListActivity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ProductListIntentService extends IntentService{

	private String[] tablicaInformacji;
	private String TAG;
	private ConnectionService cm = new ConnectionService(this);
	
	public ProductListIntentService() {
		super("ProductListIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "uruchomiono metodê onHandleIntent");
		Bundle daneZIntencji = intent.getExtras();
		Log.i(TAG, String.valueOf(daneZIntencji.getInt("categoryNumber")));
		try{
			tablicaInformacji = cm.getProductsByCategoryId(daneZIntencji.getInt("categoryNumber"));
		}catch(Exception e){
			Log.i(TAG, "problem z internetem podczas pobierania danych");
			tablicaInformacji = null;
		}

		Log.i(TAG, "po pobraniu nowoœci");

		Intent intencja = new Intent(ProductListActivity.ACTION_DOWNLOADED_PRODUCTLIST);
		Bundle mojBundle = new Bundle(2);
		mojBundle.putStringArray("DOWNLOADED_CONTENT", tablicaInformacji);
		intencja.putExtras(mojBundle);
		sendBroadcast(intencja);
		Log.i(TAG, "intencja wys³ana na broadcast");
	}

}
