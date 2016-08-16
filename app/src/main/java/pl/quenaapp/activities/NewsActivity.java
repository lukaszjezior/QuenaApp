package pl.quenaapp.activities;

import java.util.ArrayList;

import pl.quenaapp.R;
import pl.quenaapp.model.Product;
import pl.quenaapp.services.ConnectionService;
import pl.quenaapp.services.NewsIntentService;
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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;

@SuppressLint("NewApi")
public class NewsActivity extends Activity {
	public static final String ACTION_DOWNLOADED_NEWS = "pl.quenaapp.ACTION_DOWNLOADED_NEWS";
	private LinearLayout progressLayout, noInternetLayout, noProductsLayout;
	private ListView newsListView;
	private String TAG;
	private String[] newsTableReturnedFromService;

	private ConnectionService cm = new ConnectionService(this);
	private ArrayList<Product> newsList = new ArrayList<Product>();
	private SharedPreferences shPref;
	private SharedPreferencesManager shPrefManager;
	private VisibilityManager visibilityManager = new VisibilityManager();
	private NewsBroadcastReceiver newsBroadcastReceiver;
	private Checker checker = new Checker(this);
	private NewsIntentService newsIntentService = new NewsIntentService();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);

		progressLayout = (LinearLayout) findViewById(R.id.linearLayoutProgressBar);
		noInternetLayout = (LinearLayout) findViewById(R.id.linearLayoutNoInternet);
		noProductsLayout = (LinearLayout) findViewById(R.id.linearLayoutNoProducts);
		newsListView = (ListView) findViewById(R.id.listViewNowosci);
		
		shPref = getSharedPreferences("preferences", 0);
		shPrefManager = new SharedPreferencesManager(shPref);
		
		if (checker.checkAvailabilityOfInternetConnection() == true) {
			boolean tmp = shPrefManager.getNewsLoadedVar();
			if (shPrefManager.getNewsLoadedVar() == true) {
				// stworzenie listy i wyœwietlenie produktów
				newsTableReturnedFromService = savedInstanceState
						.getStringArray("tablicaNowosci");
				convertNewsTableToNewsList();

				ArrayAdapter<Product> newsAdapter = new NewsAdapter();
				if (checker.checkAvailabilityOfProducts(newsList) == true) {
					visibilityManager.showProductsListView(noProductsLayout, progressLayout, noInternetLayout, newsListView);
					newsListView.setAdapter(newsAdapter);
					handlingButtons();
				} else if (checker.checkAvailabilityOfProducts(newsList) == false) {
					visibilityManager.showNoProductsLayout(noProductsLayout, progressLayout, noInternetLayout, newsListView);
				}
			} else {
				if (checker.checkThatServiceIsWorking(newsIntentService) == true) {
					// ustaw kó³eczka loading
					visibilityManager.showLoadingLayout(noProductsLayout, progressLayout, noInternetLayout, newsListView);

					// uruchom broadcast receiver i ka¿ mu czekaæ na tablicê
					newsBroadcastReceiver = new NewsBroadcastReceiver();
					IntentFilter filter = new IntentFilter(
							ACTION_DOWNLOADED_NEWS);
					registerReceiver(newsBroadcastReceiver, filter);
				} else if (checker.checkThatServiceIsWorking(newsIntentService) == false) {
					// ustaw kó³eczka loading
					visibilityManager.showLoadingLayout(noProductsLayout, progressLayout, noInternetLayout, newsListView);

					// uruchom serwis i rozpocznij pobieranie danych
					startService(new Intent(getBaseContext(),
							NewsIntentService.class));

					// uruchom broadcast receiver i ka¿ mu czekaæ na tablicê
					newsBroadcastReceiver = new NewsBroadcastReceiver();
					IntentFilter filter = new IntentFilter(
							ACTION_DOWNLOADED_NEWS);
					registerReceiver(newsBroadcastReceiver, filter);
				}
			}
		} else {
			visibilityManager.showNoInternetLayout(noProductsLayout, progressLayout, noInternetLayout, newsListView);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(newsBroadcastReceiver);
		} catch (IllegalArgumentException e) {
			Log.i(TAG, "receiver nie jest zarejestrowany");
		}
	}

	public class NewsBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_DOWNLOADED_NEWS)) {
				Log.i(TAG, "odebrano receiverem akcjê pobrania nowosci");
				// zapis tablicy
				Bundle newsServiceBundle = intent.getExtras();
				newsTableReturnedFromService = newsServiceBundle
						.getStringArray("DOWNLOADED_CONTENT");

				// flaga ¿e wczytano dane na true
				shPrefManager.setNewsLoadedVar();
				Log.i(TAG, shPrefManager.getNewsLoadedVar().toString());
				if (checker.checkThatServiceIsWorking(newsIntentService))
					stopService(new Intent(getBaseContext(),
							NewsIntentService.class));

				// stworzenie listy i wyœwietlenie produktów
				// jeœli tablica jest null to znaczy, ¿e nie powiod³o siê
				// wczytywanie z internetu(wyœwietlamy ca³y czas
				// kó³eczko loading)
				if (newsTableReturnedFromService != null) {
					convertNewsTableToNewsList();

					// sprawdzenie czy s¹ produkty
					if (checker.checkAvailabilityOfProducts(newsList) == true) {
						ArrayAdapter<Product> newsAdapter = new NewsAdapter();
						newsListView.setAdapter(newsAdapter);
						visibilityManager.showProductsListView(noProductsLayout, progressLayout, noInternetLayout, newsListView);
						handlingButtons();
					} else if (checker.checkAvailabilityOfProducts(newsList) == false) {
						visibilityManager.showNoProductsLayout(noProductsLayout, progressLayout, noInternetLayout, newsListView);
					}

				}

				// unregister receiver
				unregisterReceiver(newsBroadcastReceiver);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i(TAG, "wywolano onsaveinstancestate");
		outState.putStringArray("tablicaNowosci", newsTableReturnedFromService);
	}

	private void convertNewsTableToNewsList() {
		for (int i = 0; i < newsTableReturnedFromService.length; i++) {
			String[] tmp = newsTableReturnedFromService[i].split("---------");

			// tmp[0] = produktid
			// tmp[1] = index
			// tmp[2] = zdjecie
			// tmp[3] = nazwa
			// tmp[4] = cena
			// tmp[5] = opis_mini
			newsList.add(new Product(Integer.parseInt(tmp[0]), tmp[1],
					tmp[2], tmp[3], Double.parseDouble(tmp[4]), tmp[5]));
			Log.i(TAG, newsList.get(i).getProductId() + " "
					+ newsList.get(i).getIndex() + " "
					+ newsList.get(i).getPathToPhoto() + " "
					+ newsList.get(i).getName() + " "
					+ newsList.get(i).getClass() + " ");
		}
	}

	private void handlingButtons() {
		newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View viewClicked,
					int position, long id) {

				Product clickedNews = newsList.get(position);

				int idProduktu = clickedNews.getProductId();
				String link = cm.getSerwer()
						+ clickedNews.getPathToPhoto();
				String name = clickedNews.getName();

				Log.i(TAG, "Id klikniêtego pokoju: " + idProduktu);
				Log.i(TAG, "Opis: " + name);
				Log.i(TAG, "Link do zdjêcia: " + link);

				Intent productDetailsIntent = new Intent(NewsActivity.this,
						ProductDetailsActivity.class);
				productDetailsIntent.putExtra("produktId", idProduktu);
				productDetailsIntent.putExtra("produktTytul", name);
				productDetailsIntent.putExtra("zdjecieSciezka", link);
				productDetailsIntent.putExtra("opisMini", clickedNews.getShortDescription());
				productDetailsIntent.putExtra("cenaBrutto",
						clickedNews.getPrice());
				shPrefManager.resetNewsLoadedVar();
				finish();
				startActivity(productDetailsIntent);
			}

		});
	}

	private class NewsAdapter extends ArrayAdapter<Product> {

		TextView index, name, price;
		ImageView imageView;
		Product product;
		ImageLoader imageLoader = new ImageLoader(NewsActivity.this);

		public NewsAdapter() {
			super(NewsActivity.this, R.layout.activity_product, newsList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(
						R.layout.activity_product, parent, false);
			}

			product = newsList.get(position);

			index = (TextView) itemView
					.findViewById(R.id.product_textViewIndex);
			name = (TextView) itemView
					.findViewById(R.id.product_textViewName);
			price = (TextView) itemView.findViewById(R.id.product_textViewPrice);
			imageView = (ImageView) itemView
					.findViewById(R.id.product_imageView);

			index.setText(product.getIndex());
			name.setText(product.getName());
			price.setText(getString(R.string.product_price) + ": " + String.valueOf(product.getPrice())
					+ " " + getString(R.string.product_coinage));

			String url = cm.getSerwer() + product.getPathToPhoto();
			Log.i(TAG, url);

			imageLoader.DisplayImage(url, imageView);

			return itemView;

		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Log.i(TAG, "wciœniêto back");
		shPrefManager.resetNewsLoadedVar();
		finish();
	}
	
	private static final int MENU_NEWS = Menu.FIRST;
	private static final int MENU_CATEGORIES = Menu.FIRST + 1;
	private static final int MENU_WEBPAGE = Menu.FIRST + 2;
	private static final int MENU_CONTACT = Menu.FIRST + 3;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MENU_NEWS, Menu.NONE, getString(R.string.activity_news_name)).setIcon(
				R.drawable.news_black);
		menu.add(Menu.NONE, MENU_CATEGORIES, Menu.NONE, getString(R.string.activity_categories_name)).setIcon(
				R.drawable.categories_black);
		menu.add(Menu.NONE, MENU_WEBPAGE, Menu.NONE, getString(R.string.webpage_title)).setIcon(
				R.drawable.webpage_black);
		menu.add(Menu.NONE, MENU_CONTACT, Menu.NONE, getString(R.string.activity_contact_name)).setIcon(
				R.drawable.contact_black);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_NEWS:
			Intent mainActivityIntent = new Intent(this, NewsActivity.class);
			shPrefManager.resetNewsLoadedVar();
			try {
				unregisterReceiver(newsBroadcastReceiver);
			} catch (IllegalArgumentException e) {
				Log.i(TAG, "receiver nie jest zarejestrowany");
			}		
			if(checker.checkThatServiceIsWorking(newsIntentService))
				newsIntentService.stopSelf();
			finish();
			startActivity(mainActivityIntent);

			break;

		case MENU_CATEGORIES:
			Intent categoryIntent = new Intent(this, CategoryActivity.class);
			categoryIntent.putExtra("rodzicNumer", 0);
			shPrefManager.resetNewsLoadedVar();
			try {
				unregisterReceiver(newsBroadcastReceiver);
			} catch (IllegalArgumentException e) {
				Log.i(TAG, "receiver nie jest zarejestrowany");
			}			
			finish();
			if(checker.checkThatServiceIsWorking(newsIntentService))
				newsIntentService.stopSelf();
			startActivity(categoryIntent);

			break;

		case MENU_WEBPAGE:
			Intent webpageIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://www.e-struna.pl"));
			shPrefManager.resetNewsLoadedVar();
			try {
				unregisterReceiver(newsBroadcastReceiver);
			} catch (IllegalArgumentException e) {
				Log.i(TAG, "receiver nie jest zarejestrowany");
			}			
			finish();
			if(checker.checkThatServiceIsWorking(newsIntentService))
				newsIntentService.stopSelf();
			startActivity(webpageIntent);

			break;

		case MENU_CONTACT:
			Intent contactIntent = new Intent(this, ContactActivity.class);
			shPrefManager.resetNewsLoadedVar();
			try {
				unregisterReceiver(newsBroadcastReceiver);
			} catch (IllegalArgumentException e) {
				Log.i(TAG, "receiver nie jest zarejestrowany");
			}
			if(checker.checkThatServiceIsWorking(newsIntentService))
				newsIntentService.stopSelf();
			finish();
			startActivity(contactIntent);
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}

}
