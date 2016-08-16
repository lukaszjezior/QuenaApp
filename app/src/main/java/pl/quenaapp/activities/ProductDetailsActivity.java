package pl.quenaapp.activities;

import pl.quenaapp.R;
import pl.quenaapp.services.ConnectionService;
import pl.quenaapp.services.ProductDetailsIntentService;
import pl.quenaapp.utils.Checker;
import pl.quenaapp.utils.SharedPreferencesManager;
import pl.quenaapp.utils.VisibilityManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProductDetailsActivity extends Activity implements OnClickListener {

	public static final String ACTION_DOWNLOADED_IMAGE = "pl.quenaapp.ACTION_DOWNLOADED_IMAGE";
	public static final int produktId = 0;
	public static final String produktTytul = "";
	public static final String zdjecieSciezka = "";
	public static final String opisMini = "";
	public static final double cenaBrutto = 0.0;
	private ImageView photoImageView;
	private TextView nameTextView, shortDescriptionTextView, priceTextView;
	private Button longDescriptionButton;
	private ImageButton navigationNewsButton, navigationCategoryButton,
			navigationWebpageButton, navigationContactButton;
	private LinearLayout progressLayout, noInternetLayout, noPhotoLayout;
	private SharedPreferences shPref;
	private SharedPreferencesManager shPrefManager;
	private ProductDetailsBroadcastReceiver productDetailsBroadcastReceiver;
	private Checker checker = new Checker(this);
	private ConnectionService connectionService = new ConnectionService(this);
	private ProductDetailsIntentService productDetailsIntentService = new ProductDetailsIntentService();
	private VisibilityManager visibilityManager = new VisibilityManager();

	private int prid;
	private String name;
	private String pathToPhoto;
	private String shortDescription;
	private Bitmap photo;
	private double price;
	private String TAG;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_details);
		nameTextView = (TextView) findViewById(R.id.textViewName);
		shortDescriptionTextView = (TextView) findViewById(R.id.textViewShortDescription);
		priceTextView = (TextView) findViewById(R.id.textViewPrice);
		photoImageView = (ImageView) findViewById(R.id.photoImageButton);
		longDescriptionButton = (Button) findViewById(R.id.longDescriptionButton);
		navigationNewsButton = (ImageButton) findViewById(R.id.navigationNewsButton);
		navigationCategoryButton = (ImageButton) findViewById(R.id.navigationCategoriesButton);
		navigationWebpageButton = (ImageButton) findViewById(R.id.navigationWebpageButton);
		navigationContactButton = (ImageButton) findViewById(R.id.navigationContactButton);
		progressLayout = (LinearLayout) findViewById(R.id.progressBarLayout);
		noInternetLayout = (LinearLayout) findViewById(R.id.noInternetLayout);
		noPhotoLayout = (LinearLayout) findViewById(R.id.noPhotoLayout);

		Intent intencja = getIntent();
		Bundle daneZIntencji = intencja.getExtras();
		prid = daneZIntencji.getInt("produktId");
		name = daneZIntencji.getString("produktTytul");
		pathToPhoto = daneZIntencji.getString("zdjecieSciezka");
		shortDescription = daneZIntencji.getString("opisMini");
		price = daneZIntencji.getDouble("cenaBrutto");

		longDescriptionButton.setOnClickListener(this);
		photoImageView.setOnClickListener(this);
		navigationNewsButton.setOnClickListener(this);
		navigationCategoryButton.setOnClickListener(this);
		navigationWebpageButton.setOnClickListener(this);
		navigationContactButton.setOnClickListener(this);

		nameTextView.setText(name);
		shortDescriptionTextView.setText(shortDescription);
		priceTextView.setText(getString(R.string.product_price) + ": "
				+ String.valueOf(price) + " "
				+ getString(R.string.product_coinage));

		shPref = getSharedPreferences("preferences", 0);
		shPrefManager = new SharedPreferencesManager(shPref);

		IntentFilter filter = new IntentFilter(ACTION_DOWNLOADED_IMAGE);
		productDetailsBroadcastReceiver = new ProductDetailsBroadcastReceiver();

		if (checker.checkAvailabilityOfInternetConnection() == true) {
			if (shPrefManager.getPhotoLoadedVar() == true) {
				// pobranie zdjecia produktu z bundle'a
				photo = savedInstanceState.getParcelable("zdjecieProduktu");
				photoImageView.setImageBitmap(photo);

				if (photo != null) {
					visibilityManager.showPhotoImageView(noPhotoLayout,
							progressLayout, noInternetLayout, photoImageView);
				} else {
					visibilityManager.showNoPhotoLayout(noPhotoLayout,
							progressLayout, noInternetLayout, photoImageView);
				}
			} else {
				if (checker
						.checkThatServiceIsWorking(productDetailsIntentService) == true) {
					// ustawiamy kó³eczka loading
					visibilityManager.showLoadingLayout(noPhotoLayout,
							progressLayout, noInternetLayout, photoImageView);

					// uruchom broadcast receiver i ka¿ mu czekaæ na tablicê
					registerReceiver(productDetailsBroadcastReceiver, filter);
				} else if (checker
						.checkThatServiceIsWorking(productDetailsIntentService) == false) {
					// ustawiamy kó³eczka loading
					visibilityManager.showLoadingLayout(noPhotoLayout,
							progressLayout, noInternetLayout, photoImageView);

					// uruchom serwis i rozpocznij pobieranie danych
					Intent intentService = new Intent(getBaseContext(),
							ProductDetailsIntentService.class);
					intentService.putExtra("photoPath", pathToPhoto);
					startService(intentService);

					// uruchom broadcast receiver i ka¿ mu czekaæ na tablicê
					registerReceiver(productDetailsBroadcastReceiver, filter);
				}
			}
		} else {
			visibilityManager.showNoInternetLayout(noPhotoLayout,
					progressLayout, noInternetLayout, photoImageView);
		}
	}

	public class ProductDetailsBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_DOWNLOADED_IMAGE)) {
				Log.i(TAG, "odebrano receiverem akcjê pobrania obrazka");

				Bundle serviceBundle = intent.getExtras();
				photo = (Bitmap) serviceBundle.getParcelable("photo");

				shPrefManager.setPhotoLoadedVar();
				if (checker
						.checkThatServiceIsWorking(productDetailsIntentService)) {
					stopService(new Intent(getBaseContext(),
							ProductDetailsIntentService.class));
				}

				if (photo != null) {
					photoImageView.setImageBitmap(photo);
					visibilityManager.showPhotoImageView(noPhotoLayout,
							progressLayout, noInternetLayout, photoImageView);
				} else {
					Log.i(TAG, "zdjêcie jest nullem! a to heca.");
					visibilityManager.showNoPhotoLayout(noPhotoLayout,
							progressLayout, noInternetLayout, photoImageView);
				}

				// unregister receiver
				unregisterReceiver(productDetailsBroadcastReceiver);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Log.i(TAG, "wywolano onsaveinstancestate");
		outState.putParcelable("zdjecieProduktu", photo);
	}

	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		shPrefManager.resetPhotoLoadedVar();
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(productDetailsBroadcastReceiver);
		} catch (IllegalArgumentException e) {
			Log.i(TAG, "receiver nie jest zarejestrowany");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.navigationNewsButton:
			Intent newsActivityIntent = new Intent(this, NewsActivity.class);
			shPrefManager.resetPhotoLoadedVar();
			shPrefManager.resetCategoriesLoadedVar();
			shPrefManager.resetProductsLoadedVar();
			finishOtherActivitiesAndServices();
			finish();
			startActivity(newsActivityIntent);

			break;

		case R.id.navigationCategoriesButton:
			Intent categoriesIntent = new Intent(this, CategoryActivity.class);
			categoriesIntent.putExtra("rodzicNumer", 0);
			shPrefManager.resetPhotoLoadedVar();
			shPrefManager.resetCategoriesLoadedVar();
			shPrefManager.resetProductsLoadedVar();
			finishOtherActivitiesAndServices();
			finish();
			startActivity(categoriesIntent);
			break;

		case R.id.navigationWebpageButton:
			Intent webpageIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://www.e-struna.pl"));
			shPrefManager.resetPhotoLoadedVar();
			shPrefManager.resetCategoriesLoadedVar();
			shPrefManager.resetProductsLoadedVar();
			finishOtherActivitiesAndServices();
			finish();
			startActivity(webpageIntent);

			break;

		case R.id.navigationContactButton:
			Intent contactIntent = new Intent(this, ContactActivity.class);
			shPrefManager.resetPhotoLoadedVar();
			shPrefManager.resetCategoriesLoadedVar();
			shPrefManager.resetProductsLoadedVar();
			finishOtherActivitiesAndServices();
			finish();
			startActivity(contactIntent);

			break;

		case R.id.longDescriptionButton:
			if (checker.checkAvailabilityOfInternetConnection() == true) {
				Intent longDescriptionIntent = new Intent(this,
						ProductDescriptionActivity.class);
				longDescriptionIntent.putExtra("produktId", String.valueOf(prid));
				startActivity(longDescriptionIntent);
			} else {
				Toast.makeText(getApplicationContext(),
						"Brak dostêpu do internetu!", Toast.LENGTH_SHORT)
						.show();
			}

			break;

		case R.id.photoImageButton:
			if (checker.checkAvailabilityOfInternetConnection() == true) {
				Intent largePhotoIntent = new Intent(this,
						ProductPhotoActivity.class);
				largePhotoIntent.putExtra("zdjecieSciezka", pathToPhoto);
				startActivity(largePhotoIntent);
			} else {
				Toast.makeText(getApplicationContext(),
						"Brak dostêpu do internetu!", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}

	}
	
	private void finishOtherActivitiesAndServices(){
		if(CategoryActivity.activity != null){
			if(checker.checkThatServiceIsWorking(CategoryActivity.categoryIntentService))
				CategoryActivity.categoryIntentService.stopSelf();
			CategoryActivity.activity.finish();
		}
		if(ProductListActivity.activity != null){
			if(checker.checkThatServiceIsWorking(ProductListActivity.productListIntentService))
				ProductListActivity.productListIntentService.stopSelf();
			ProductListActivity.activity.finish();
		}
	}

}
