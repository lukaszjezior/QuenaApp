package pl.quenaapp.activities;

import java.util.ArrayList;

import pl.quenaapp.R;
import pl.quenaapp.model.Product;
import pl.quenaapp.services.ConnectionService;
import pl.quenaapp.services.ProductListIntentService;
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

public class ProductListActivity extends Activity {
	public static final String ACTION_DOWNLOADED_PRODUCTLIST = "pl.quenaapp.ACTION_DOWNLOADED_PRODUCTLIST";
	public static final int rodzicNumer = 0;
	public static Activity activity = null;
	
	private LinearLayout progressLayout, noInternetLayout, noProductsLayout;
	private ListView productListView;

	private ArrayList<Product> productList = new ArrayList<Product>();
	private String[] productTableReturnedFromService;
	private String TAG;
	private int category = 0;

	private VisibilityManager visibilityManager = new VisibilityManager();
	private SharedPreferences shPref;
	private SharedPreferencesManager shPrefManager;
	private ProductListBroadcastReceiver productListBroadcastReceiver;
	private Checker checker = new Checker(this);
	private ConnectionService connectionService = new ConnectionService(this);
	public static ProductListIntentService productListIntentService = new ProductListIntentService();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_list);
		activity = this;
		progressLayout = (LinearLayout) findViewById(R.id.linearLayoutProgressBar);
		noInternetLayout = (LinearLayout) findViewById(R.id.linearLayoutNoInternet);
		productListView = (ListView) findViewById(R.id.listViewProduktow);
		noProductsLayout = (LinearLayout) findViewById(R.id.linearLayoutNoProducts);

		Intent intencja = getIntent();
		Bundle daneZIntencji = intencja.getExtras();
		category = daneZIntencji.getInt("categoryNumber");

		shPref = getSharedPreferences("preferences", 0);
		shPrefManager = new SharedPreferencesManager(shPref);

		if (checker.checkAvailabilityOfInternetConnection() == true) {
			if (shPrefManager.getProductsLoadedVar() == true) {
				// stworzenie listy i wyœwietlenie produktów
				productTableReturnedFromService = savedInstanceState
						.getStringArray("tablicaInformacji");
				convertProductTableToProductList();

				ArrayAdapter<Product> adapter = new ProductListAdapter();
				if (checker.checkAvailabilityOfProducts(productList) == true) {
					visibilityManager.showProductsListView(noProductsLayout, progressLayout, noInternetLayout, productListView);
					productListView.setAdapter(adapter);
					handlingButtons();
				} else if (checker.checkAvailabilityOfProducts(productList) == false) {
					visibilityManager.showNoProductsLayout(noProductsLayout, progressLayout, noInternetLayout, productListView);
				}
			} else {
				if (checker.checkThatServiceIsWorking(productListIntentService) == true) {
					// ustaw kó³eczka loading
					visibilityManager.showLoadingLayout(noProductsLayout, progressLayout, noInternetLayout, productListView);

					// uruchom broadcast receiver i ka¿ mu czekaæ na tablicê
					productListBroadcastReceiver = new ProductListBroadcastReceiver();
					IntentFilter filter = new IntentFilter(
							ACTION_DOWNLOADED_PRODUCTLIST);
					registerReceiver(productListBroadcastReceiver, filter);
				} else if (checker.checkThatServiceIsWorking(productListIntentService) == false) {
					// ustaw kó³eczka loading
					visibilityManager.showLoadingLayout(noProductsLayout, progressLayout, noInternetLayout, productListView);

					// uruchom serwis i rozpocznij pobieranie danych
					Intent intentService = new Intent(getBaseContext(),
							ProductListIntentService.class);
					intentService.putExtra("categoryNumber", category);
					startService(intentService);

					// uruchom broadcast receiver i ka¿ mu czekaæ na tablicê
					productListBroadcastReceiver = new ProductListBroadcastReceiver();
					IntentFilter filter = new IntentFilter(
							ACTION_DOWNLOADED_PRODUCTLIST);
					registerReceiver(productListBroadcastReceiver, filter);
				}
			}
		} else {
			visibilityManager.showNoInternetLayout(noProductsLayout, progressLayout, noInternetLayout, productListView);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(productListBroadcastReceiver);
		} catch (IllegalArgumentException e) {
			Log.i(TAG, "receiver nie jest zarejestrowany");
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		shPrefManager.resetProductsLoadedVar();
		finish();
	}

	public class ProductListBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_DOWNLOADED_PRODUCTLIST)) {
				Log.i(TAG, "odebrano receiverem akcjê pobrania listy produktów");
				// zapis tablicy
				Bundle serviceBundle = intent.getExtras();
				productTableReturnedFromService = serviceBundle
						.getStringArray("DOWNLOADED_CONTENT");

				// flaga ¿e wczytano dane na true
				shPrefManager.setProductsLoadedVar();
				Log.i(TAG, shPrefManager.getProductsLoadedVar().toString());
				if (checker.checkThatServiceIsWorking(productListIntentService))
					stopService(new Intent(getBaseContext(),
							ProductListIntentService.class));

				// stworzenie listy i wyœwietlenie produktów
				if (productTableReturnedFromService != null) {
					convertProductTableToProductList();

					// sprawdzenie czy s¹ produkty
					if (checker.checkAvailabilityOfProducts(productList)) {
						ArrayAdapter<Product> adapter = new ProductListAdapter();
						productListView.setAdapter(adapter);
						visibilityManager.showProductsListView(noProductsLayout, progressLayout, noInternetLayout, productListView);
						handlingButtons();
					} else if (!checker.checkAvailabilityOfProducts(productList)) {
						visibilityManager.showNoProductsLayout(noProductsLayout, progressLayout, noInternetLayout, productListView);
					}
				}

				// unregister receiver
				unregisterReceiver(productListBroadcastReceiver);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Log.i(TAG, "wywolano onsaveinstancestate");
		outState.putStringArray("tablicaInformacji", productTableReturnedFromService);
		outState.putInt("pozycjaX", productListView.getScrollX());
		outState.putInt("pozycjaY", productListView.getScrollY());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		productListView.scrollTo(savedInstanceState.getInt("pozycjaX"),
				savedInstanceState.getInt("pozycjaY"));
	}

	private void convertProductTableToProductList() {
		for (int i = 0; i < productTableReturnedFromService.length; i++) {
			String[] productTable = productTableReturnedFromService[i].split("---------");

			// tmp[0] = produktid
			// tmp[1] = index
			// tmp[2] = zdjecie
			// tmp[3] = nazwa
			// tmp[4] = cena
			// tmp[5] = opis_mini
			productList.add(new Product(Integer.parseInt(productTable[0]), productTable[1],
					productTable[2], productTable[3], Double.parseDouble(productTable[4]), productTable[5]));
			// Log.i(TAG, listaProduktow.get(i).getProduktId() + " "
			// + listaProduktow.get(i).getIndex() + " "
			// + listaProduktow.get(i).getZdjecieSciezka() + " "
			// + listaProduktow.get(i).getNazwa() + " "
			// + listaProduktow.get(i).getClass() + " ");
		}
	}

	private void handlingButtons() {
		productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View viewClicked,
					int position, long id) {

				Product clickedProduct = productList.get(position);

				int idProduktu = clickedProduct.getProductId();
				String link = connectionService.getSerwer()
						+ clickedProduct.getPathToPhoto();
				String opis = clickedProduct.getName();

				Intent productDetailsIntent = new Intent(ProductListActivity.this,
						ProductDetailsActivity.class);
				productDetailsIntent.putExtra("produktId", idProduktu);
				productDetailsIntent.putExtra("produktTytul", opis);
				productDetailsIntent.putExtra("zdjecieSciezka", link);
				productDetailsIntent.putExtra("opisMini", clickedProduct.getShortDescription());
				productDetailsIntent.putExtra("cenaBrutto",
						clickedProduct.getPrice());
				startActivity(productDetailsIntent);
			}

		});
	}

	private class ProductListAdapter extends ArrayAdapter<Product> {

		TextView index, name, price;
		ImageView imageView;
		Product product;
		ImageLoader imageLoader = new ImageLoader(ProductListActivity.this);
		String url;
		
		public ProductListAdapter() {
			super(ProductListActivity.this, R.layout.activity_product, productList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(R.layout.activity_product,
						parent, false);
			}

			product = productList.get(position);

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

			url = connectionService.getSerwer() + product.getPathToPhoto();
			
			imageLoader.DisplayImage(url, imageView);
			
			return itemView;

		}
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
			Intent newsIntent = new Intent(this, NewsActivity.class);
			shPrefManager.resetProductsLoadedVar();
			shPrefManager.resetCategoriesLoadedVar();
			finishCategoryActivityAndService();
			finish();
			startActivity(newsIntent);

			break;

		case MENU_CATEGORIES:
			Intent categoryIntent = new Intent(this, CategoryActivity.class);
			categoryIntent.putExtra("rodzicNumer", 0);
			shPrefManager.resetProductsLoadedVar();
			shPrefManager.resetCategoriesLoadedVar();
			finishCategoryActivityAndService();
			finish();
			startActivity(categoryIntent);

			break;

		case MENU_WEBPAGE:
			Intent webpageIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://www.e-struna.pl"));
			shPrefManager.resetProductsLoadedVar();
			shPrefManager.resetCategoriesLoadedVar();
			finishCategoryActivityAndService();
			finish();
			startActivity(webpageIntent);

			break;

		case MENU_CONTACT:
			Intent contactIntent = new Intent(this, ContactActivity.class);
			shPrefManager.resetProductsLoadedVar();
			shPrefManager.resetCategoriesLoadedVar();
			finishCategoryActivityAndService();
			finish();
			startActivity(contactIntent);

			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}
	
	private void finishCategoryActivityAndService(){
		if(CategoryActivity.activity != null){
			if(checker.checkThatServiceIsWorking(CategoryActivity.categoryIntentService))
				CategoryActivity.categoryIntentService.stopSelf();
			CategoryActivity.activity.finish();
		}
	}
}
