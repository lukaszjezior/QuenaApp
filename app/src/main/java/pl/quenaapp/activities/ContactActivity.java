package pl.quenaapp.activities;

import pl.quenaapp.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class ContactActivity extends Activity implements OnClickListener {

	ImageButton phoneButtonOne, phoneButtonTwo, emailButtonOne, emailButtonTwo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		phoneButtonOne = (ImageButton) findViewById(R.id.phoneNumberOne);
		phoneButtonTwo = (ImageButton) findViewById(R.id.phoneNumberTwo);
		emailButtonOne = (ImageButton) findViewById(R.id.emailNumberOne);
		emailButtonTwo = (ImageButton) findViewById(R.id.emailNumberTwo);

		phoneButtonOne.setOnClickListener(this);
		phoneButtonTwo.setOnClickListener(this);
		emailButtonOne.setOnClickListener(this);
		emailButtonTwo.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.phoneNumberOne:
			Intent callIntentOne = new Intent(Intent.ACTION_CALL);
			callIntentOne.setData(Uri.parse("tel:727679971"));
			finish();
			startActivity(callIntentOne);

			break;

		case R.id.phoneNumberTwo:
			Intent callIntentTwo = new Intent(Intent.ACTION_CALL);
			callIntentTwo.setData(Uri.parse("tel:790676228"));
			finish();
			startActivity(callIntentTwo);

			break;

		case R.id.emailNumberOne:
			Uri uriOne = Uri.parse("mailto:sklep@e-struna.pl");
			Intent emailIntentOne = new Intent(Intent.ACTION_SENDTO, uriOne);
			emailIntentOne.putExtra(Intent.EXTRA_SUBJECT,
					"Pytanie do obs³ugi sklepu e-struna");
			finish();
			startActivity(emailIntentOne);

			break;

		case R.id.emailNumberTwo:
			Uri uriTwo = Uri.parse("mailto:mfidecki@gmail.com");
			Intent emailIntentTwo = new Intent(Intent.ACTION_SENDTO, uriTwo);
			emailIntentTwo.putExtra(Intent.EXTRA_SUBJECT,
					"Pytanie do obs³ugi sklepu e-struna");
			finish();
			startActivity(emailIntentTwo);

			break;

		default:
			break;
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
			Intent mainActivityIntent = new Intent(this, NewsActivity.class);
			finish();
			startActivity(mainActivityIntent);

			break;

		case MENU_CATEGORIES:
			Intent categoryIntent = new Intent(this, CategoryActivity.class);
			categoryIntent.putExtra("rodzicNumer", 0);
			finish();
			startActivity(categoryIntent);

			break;

		case MENU_WEBPAGE:
			Intent webpageIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://www.e-struna.pl"));
			startActivity(webpageIntent);

			break;

		case MENU_CONTACT:
			Intent contactIntent = new Intent(this, ContactActivity.class);
			finish();
			startActivity(contactIntent);

			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}

}
