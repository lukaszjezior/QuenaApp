package pl.quenaapp.activities;

import pl.quenaapp.R;
import pl.quenaapp.utils.SharedPreferencesManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends Activity {

	private SharedPreferences shPref;
	private SharedPreferencesManager shPrefManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		shPref = getSharedPreferences("preferences", 0);
		shPrefManager = new SharedPreferencesManager(shPref);
		shPrefManager.resetAllVariables();
		
		new Thread() {
			public void run() {
				try {
					sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Intent intencja = new Intent(MainActivity.this,
									CategoryActivity.class);
							intencja.putExtra("rodzicNumer", 0);
							startActivity(intencja);
						}
					});
				}
			}
		}.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
}
