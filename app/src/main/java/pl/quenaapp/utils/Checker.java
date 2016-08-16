package pl.quenaapp.utils;

import java.util.ArrayList;

import pl.quenaapp.model.Product;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.IntentService;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Checker {

	private Context ctx;
	private String TAG;

	public Checker(Context ctx) {
		this.ctx = ctx;
	}

	public boolean checkAvailabilityOfInternetConnection() {
		ConnectivityManager cm = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public boolean checkThatServiceIsWorking(IntentService intentService) {
		ActivityManager manager = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (intentService.getClass().getName()
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public boolean checkAvailabilityOfProducts(ArrayList<Product> arrayList) {
		Product product = arrayList.get(0);
		Log.i(TAG, product.getProductId() + " " + product.getIndex() + " "
				+ product.getName());
		if (product.getProductId() == 0
				&& product.getIndex().contentEquals("None")
				&& product.getName().contentEquals("None")
				&& product.getPathToPhoto().contentEquals("None")
				&& product.getPrice() == 0.0
				&& product.getShortDescription().equals("None")) {
			return false;
		}
		return true;
	}

}
