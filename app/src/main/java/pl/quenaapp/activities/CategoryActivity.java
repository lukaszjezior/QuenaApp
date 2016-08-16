package pl.quenaapp.activities;

import java.util.ArrayList;

import pl.quenaapp.R;
import pl.quenaapp.model.Category;
import pl.quenaapp.services.CategoryIntentService;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class CategoryActivity extends Activity {
	public static final String ACTION_DOWNLOADED_CATEGORY = "pl.quenaapp.ACTION_DOWNLOADED_CATEGORY";
	public static final int parentNumber = 0;
	
	public static Activity activity = null;
	private LinearLayout progressLayout, noInternetLayout;
	private ListView categoryListView;

	private String[] categoryTableReturnedFromService;
	private String TAG;
	private int parent = 0;
	private ArrayList<Category> categoryList = new ArrayList<Category>();
	private ArrayList<String> categoryGroupList = new ArrayList<String>();

	private VisibilityManager visibilityManager = new VisibilityManager();
	private IntentFilter filter = new IntentFilter(ACTION_DOWNLOADED_CATEGORY);
	public static CategoryIntentService categoryIntentService = new CategoryIntentService();
	private Checker checker = new Checker(this);
	private SharedPreferences shPref;
	private SharedPreferencesManager shPrefManager;
	private CategoryBroadcastReceiver categoryBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_list);
		activity = this;
		progressLayout = (LinearLayout) findViewById(R.id.linearLayoutProgressBar);
		noInternetLayout = (LinearLayout) findViewById(R.id.linearLayoutNoInternet);
		categoryListView = (ListView) findViewById(R.id.listViewProduktow);
		
		if (savedInstanceState != null) {
			categoryGroupList = savedInstanceState
					.getStringArrayList("categoryGroupList");
			writeLogCategoryGroupList();
		}

		Intent intent = getIntent();
		Bundle dataFromIntent = intent.getExtras();
		parent = dataFromIntent.getInt("parentNumber");

		shPref = getSharedPreferences("preferences", 0);
		shPrefManager = new SharedPreferencesManager(shPref);

		if (checker.checkAvailabilityOfInternetConnection() == true) {
			if (shPrefManager.getCategoriesLoadedVar() == true) {
				// stworzenie listy i wyœwietlenie produktów

				// TU JEST PROBLEM. TABLICA KATEGORII ZAWIERA WARTOŒÆ
				// POPRZEDNIEJ LISTY PRZY COFANIU.
				// TRZEBA ZROBIÆ TAK, ABY ZAWSZE ZAWIERA£A OSTATNI ELEMENT Z
				// LISTYGRUP.
				// problem rozwi¹zany
				// w przypadku obrotu ekranu poci¹gamy ostatni element z
				// listyGrup
				// który zosta³ zapisany do bundle'a i u¿ywamy go do stworzenia
				// listy przez adapter.
				categoryList = convertCategoryListStringToArrayList(categoryGroupList
						.get(categoryGroupList.size() - 1));
				ArrayAdapter<Category> adapter = new CategoryAdapter();
				categoryListView.setAdapter(adapter);
				visibilityManager.showCategoriesListView(progressLayout, noInternetLayout, categoryListView);

				handlingButtons();
			} else {
				if (checker.checkThatServiceIsWorking(categoryIntentService) == true) {
					// ustaw kó³eczka loading
					visibilityManager.showLoadingLayout(progressLayout, noInternetLayout, categoryListView);

					// uruchom broadcast receiver i ka¿ mu czekaæ na tablicê
					categoryBroadcastReceiver = new CategoryBroadcastReceiver();
					registerReceiver(categoryBroadcastReceiver, filter);
				} else if (checker.checkThatServiceIsWorking(categoryIntentService) == false) {
					// ustaw kó³eczka loading
					visibilityManager.showLoadingLayout(progressLayout, noInternetLayout, categoryListView);

					// uruchom serwis i rozpocznij pobieranie danych
					Intent intentService = new Intent(getBaseContext(),
							CategoryIntentService.class);
					intentService.putExtra("parentNumber", parent);
					startService(intentService);

					// uruchom broadcast receiver i ka¿ mu czekaæ na tablicê
					categoryBroadcastReceiver = new CategoryBroadcastReceiver();
					IntentFilter filter = new IntentFilter(
							ACTION_DOWNLOADED_CATEGORY);
					registerReceiver(categoryBroadcastReceiver, filter);
				}
			}
		} else {
			visibilityManager.showNoInternetLayout(progressLayout, noInternetLayout, categoryListView);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putStringArray("tablicaKategorii",
				categoryTableReturnedFromService);
		outState.putStringArrayList("categoryGroupList", categoryGroupList);
	}

	public String convertCategoryArrayListToString(
			ArrayList<Category> categoryList) {
		String result = "";
		for (int i = 0; i < categoryList.size(); i++) {
			result += categoryList.get(i).getCategoryNumber() + "-----";
			result += categoryList.get(i).getParentNumber() + "-----";
			result += categoryList.get(i).getTitle() + "-----";
			result += categoryList.get(i).getChildsCount();
			if (i != categoryList.size() - 1)
				result += "/////";
		}
		// Log.i(TAG, result);
		return result;
	}

	public ArrayList<Category> convertCategoryListStringToArrayList(
			String categoryListString) {
		ArrayList<Category> result = new ArrayList<Category>();

		String[] categoryListArray = categoryListString.split("/////");
		for (int i = 0; i < categoryListArray.length; i++) {
			String[] categoryArray = categoryListArray[i].split("-----");
			result.add(new Category(Integer.parseInt(categoryArray[0]), Integer
					.parseInt(categoryArray[1]), categoryArray[2], Integer
					.parseInt(categoryArray[3])));
		}

		return result;
	}

	public class CategoryBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_DOWNLOADED_CATEGORY)) {
				Log.i(TAG, "odebrano receiverem akcjê pobrania kategorii");
				// zapis tablicy
				Bundle categoryServiceBundle = intent.getExtras();
				categoryTableReturnedFromService = categoryServiceBundle
						.getStringArray("DOWNLOADED_CONTENT");

				// flaga ¿e wczytano dane na true
				shPrefManager.setCategoriesLoadedVar();
				if (checker.checkThatServiceIsWorking(categoryIntentService))
					stopService(new Intent(getBaseContext(),
							CategoryIntentService.class));

				// stworzenie listy i wyœwietlenie produktów
				if (categoryTableReturnedFromService != null) {
					convertCategoryTableToCategoryList();
					addCategoryGroupToGroupList();
					Log.i(TAG, "DODANO GRUPÊ DO LISTY!!!");

					ArrayAdapter<Category> categoryAdapter = new CategoryAdapter();
					categoryListView.setAdapter(categoryAdapter);
					visibilityManager.showCategoriesListView(progressLayout, noInternetLayout, categoryListView);
					categoryListView.setAdapter(categoryAdapter);
					handlingButtons();
				}

				// unregister receiver
				unregisterReceiver(categoryBroadcastReceiver);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (categoryGroupList.size() > 1) {
			// usun¹æ ostatni¹ grupê z listyGrup
			categoryGroupList.remove(categoryGroupList.size() - 1);
			Log.i(TAG, "USUNIÊTO GRUPÊ Z LISTY!!!");
			// zczytaæ ostatni¹ grupê z listyGrup i zapisaæ j¹ do listyKategorii
			categoryList = convertCategoryListStringToArrayList(categoryGroupList
					.get(categoryGroupList.size() - 1));
			// nastêpnie zaznaczyæ ¿e kategorie s¹ ju¿ wczytane
			shPrefManager.setCategoriesLoadedVar();
			// nastêpnie prze³adowaæ listê
			ArrayAdapter<Category> adapter = new CategoryAdapter();
			categoryListView.setAdapter(adapter);

		} else if (categoryGroupList.size() == 1) {
//			if (!checker.czySerwisPracuje(categoryIntentService)) {
//				categoryGroupList.removeAll(categoryGroupList);
//				uruchomPobieranieDanych(0);
//			}
			Log.i(TAG, "g³ówna kategoria");
		} else {
			Log.i(TAG, "PUSTA LISTA GRUP");
			startLoadingCategories(0);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(categoryBroadcastReceiver);
			Log.i(TAG, "wyrejestrowano receiver");
		} catch (IllegalArgumentException e) {
			Log.i(TAG, "receiver nie jest zarejestrowany");
		}
	}

	private void convertCategoryTableToCategoryList() {
		for (int i = 0; i < categoryTableReturnedFromService.length; i++) {
			String[] categoryTable = categoryTableReturnedFromService[i]
					.split("---------");

			// tmp[0] = muid
			// tmp[1] = rodzic
			// tmp[2] = tytul
			// tmp[3] = pozycja
			// tmp[4] = ilosc potomkow
			categoryList.add(new Category(Integer.parseInt(categoryTable[0]), Integer
					.parseInt(categoryTable[1]), categoryTable[2], Integer.parseInt(categoryTable[4])));

			// Log.i(TAG, listaKategorii.get(i).getCategoryNumber() + " "
			// + listaKategorii.get(i).getParentNumber() + " "
			// + listaKategorii.get(i).getTitle() + " "
			// + listaKategorii.get(i).getChildsCount());
		}
	}

	private void addCategoryGroupToGroupList() {
		categoryGroupList
				.add(convertCategoryArrayListToString(categoryList));
	}

	private void handlingButtons() {
		categoryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked,
					int position, long id) {

				if (categoryList.get(position).getChildsCount() > 0) {
					startLoadingCategories(categoryList.get(position)
							.getCategoryNumber());
				} else {
					Intent intencja = new Intent(CategoryActivity.this,
							ProductListActivity.class);
					intencja.putExtra("categoryNumber",
							categoryList.get(position).getCategoryNumber());
					startActivity(intencja);
				}

			}
		});
	}

	private void startLoadingCategories(int categoryNumber) {
		shPrefManager.resetCategoriesLoadedVar();
		visibilityManager.showLoadingLayout(progressLayout, noInternetLayout, categoryListView);

		Intent intencjaSerwis = new Intent(getBaseContext(),
				CategoryIntentService.class);
		intencjaSerwis.putExtra("parentNumber", categoryNumber);
		startService(intencjaSerwis);

		categoryList.removeAll(categoryList);

		categoryBroadcastReceiver = new CategoryBroadcastReceiver();
		IntentFilter filter = new IntentFilter(ACTION_DOWNLOADED_CATEGORY);
		registerReceiver(categoryBroadcastReceiver, filter);
	}

	private void writeLogCategoryGroupList() {
		ArrayList<Category> categoryList;
		for (int i = 0; i < categoryGroupList.size(); i++) {
			categoryList = convertCategoryListStringToArrayList(categoryGroupList
					.get(i));
			Log.i(TAG, "GRUPA NUMER " + i);

			for (int j = 0; j < categoryList.size(); j++) {
				Log.i(TAG, "Nr.Kat: " + categoryList.get(j).getCategoryNumber()
						+ ", Nr.Rodz: " + categoryList.get(j).getParentNumber()
						+ ", Tytu³: " + categoryList.get(j).getTitle()
						+ ", L.Pot: " + categoryList.get(j).getChildsCount());
			}

		}
	}

	private void wypiszListeKategorii(ArrayList<Category> categoryList) {
		for (int j = 0; j < categoryList.size(); j++) {
			Log.i(TAG, "Nr.Kat: " + categoryList.get(j).getCategoryNumber()
					+ ", Nr.Rodz: " + categoryList.get(j).getParentNumber()
					+ ", Tytu³: " + categoryList.get(j).getTitle()
					+ ", L.Pot: " + categoryList.get(j).getChildsCount());
		}
	}

	private class CategoryAdapter extends ArrayAdapter<Category> {

		TextView categoryTitleField;
		String categoryTitle;

		public CategoryAdapter() {
			super(CategoryActivity.this, R.layout.activity_product,
					categoryList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(
						R.layout.activity_item_category, parent, false);
			}

			categoryTitle = categoryList.get(position).getTitle();

			categoryTitleField = (TextView) itemView
					.findViewById(R.id.categoryTextView);

			categoryTitleField.setText(categoryTitle);

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
		menu.add(Menu.NONE, MENU_NEWS, Menu.NONE,
				getString(R.string.activity_news_name)).setIcon(
				R.drawable.news_black);
		menu.add(Menu.NONE, MENU_CATEGORIES, Menu.NONE,
				getString(R.string.activity_categories_name)).setIcon(
				R.drawable.categories_black);
		menu.add(Menu.NONE, MENU_WEBPAGE, Menu.NONE,
				getString(R.string.webpage_title)).setIcon(
				R.drawable.webpage_black);
		menu.add(Menu.NONE, MENU_CONTACT, Menu.NONE,
				getString(R.string.activity_contact_name)).setIcon(
				R.drawable.contact_black);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_NEWS:
			Intent intencjaNews = new Intent(this, NewsActivity.class);
			shPrefManager.resetCategoriesLoadedVar();
			finish();
			startActivity(intencjaNews);

			break;

		case MENU_CATEGORIES:
			Intent categoryIntent = new Intent(this, CategoryActivity.class);
			categoryIntent.putExtra("parentNumber", 0);
			shPrefManager.resetCategoriesLoadedVar();
			finish();
			startActivity(categoryIntent);

			break;

		case MENU_WEBPAGE:
			Intent webpageIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://www.e-struna.pl"));
			shPrefManager.resetCategoriesLoadedVar();
			finish();
			startActivity(webpageIntent);

			break;

		case MENU_CONTACT:
			Intent contactIntent = new Intent(this, ContactActivity.class);
			shPrefManager.resetCategoriesLoadedVar();
			finish();
			startActivity(contactIntent);

			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}
}
