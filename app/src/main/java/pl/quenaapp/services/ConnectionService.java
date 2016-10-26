package pl.quenaapp.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import pl.quenaapp.model.Product;

public class ConnectionService {

	private Context context;
	private String TAG;

	private String server = "http://www.e-struna.pl/";

	public ConnectionService(Context context) {
		super();
		this.context = context;
	}
	
	public String getSerwer() {
		return server;
	}

	public String[] getProductsByCategoryId(int categoryId) {
		String resultString = "";
		InputStream inputStream = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(server
					+ "android/products.php");

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("categoryNumber", String
					.valueOf(categoryId)));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			Log.i(TAG, "Nawi�zano po��czenie http");
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "Problem z po��czeniem http");
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			inputStream.close();
			resultString = sb.toString();
			Log.i(TAG, "Zczytano list� budynk�w");
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "Problem z czytaniem danych");
		}

		String[] resultTab = deleteFirstAndLastElementInTable(resultString.split("/////////"));

		return resultTab;
	}

	public String[] getCategoriesByParentNumber(int parentNumber) {
		String resultString = "";
		InputStream isr = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost http = new HttpPost(server
					+ "android/categories.php");

			List<NameValuePair> nameKey = new ArrayList<NameValuePair>(2);
			nameKey.add(new BasicNameValuePair("parentNumber", String
					.valueOf(parentNumber)));
			http.setEntity(new UrlEncodedFormEntity(nameKey, HTTP.UTF_8));

			HttpResponse response = httpclient.execute(http);
			HttpEntity entity = response.getEntity();
			isr = entity.getContent();
			Log.i(TAG, "Nawi�zano po��czenie http");
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "Problem z po��czeniem http");
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					isr, "UTF8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			isr.close();
			resultString = sb.toString();
			Log.i(TAG, "Zczytano list� budynk�w");
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "Problem z czytaniem danych");
		}

		String[] resultTab = deleteFirstAndLastElementInTable(resultString.split("/////////"));

		return resultTab;
	}
	/*
	public String[] getNews() {
		String resultString = "";
		InputStream isr = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost http = new HttpPost(server
					+ "android/news.php");

			HttpResponse response = httpclient.execute(http);
			HttpEntity entity = response.getEntity();
			isr = entity.getContent();
			Log.i(TAG, "Nawi�zano po��czenie http");
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "Problem z po��czeniem http");
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					isr, "UTF8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			isr.close();
			resultString = sb.toString();
			Log.i(TAG, "Zczytano list� budynk�w");
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "Problem z czytaniem danych");
		}

		String[] resultTab = deleteFirstAndLastElementInTable(resultString.split("/////////"));
		Log.i(TAG, "Pobrano list� nowo�ci");
		return resultTab;
	}
	*/
	private String[] deleteFirstAndLastElementInTable(String[] tableToProcess) {
		String[] tableToReturn = new String[tableToProcess.length - 2];

		for (int i = 0; i < tableToReturn.length; i++) {
			tableToReturn[i] = tableToProcess[i + 1];
		}

		return tableToReturn;
	}
	
	public Bitmap downloadImageByURL(String imageURL) {
		Bitmap imageToReturn = null;

		try {
			URL url = new URL(imageURL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			InputStream is = connection.getInputStream();
			imageToReturn = BitmapFactory.decodeStream(is);
			Log.i(TAG, "Pobrano obraz z linku: " + imageURL);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (imageToReturn == null) {
			Log.i(TAG, "Nie uda�o si� pobra� obrazu z linku: " + imageURL);
		}

		return imageToReturn;
	}


	public ArrayList<Product> getNews() {
		ArrayList<Product> products = new ArrayList<Product>();
		products.add(new Product(0, "STRUNY", "http://quena.pl/220-large_default/daddario-ej27n-12.jpg", "DADDARIO EJ27N 1/2", 25.00, "D’Addario Classics struny uczniowskie, rozmiar 1/2 naciąg średni. Wszystkie struny wiolinowe są wyrównywane przy użyciu prawnie zastrzeżonego procesu szlifowania bezkłowego. Nieprześcigniona kontrola regularności, przekroju oraz stałej średnicy gwarantuje absolutnie perfekcyjną intonację. Rektyfikowane struny znane są ze swego „ciepłego” brzmienia."));
		products.add(new Product(1, "STRUNY", "http://quena.pl/220-large_default/daddario-ej27n-12.jpg", "DADDARIO EJ27N 1/2", 25.00, "D’Addario Classics struny uczniowskie, rozmiar 1/2 naciąg średni. Wszystkie struny wiolinowe są wyrównywane przy użyciu prawnie zastrzeżonego procesu szlifowania bezkłowego. Nieprześcigniona kontrola regularności, przekroju oraz stałej średnicy gwarantuje absolutnie perfekcyjną intonację. Rektyfikowane struny znane są ze swego „ciepłego” brzmienia."));
		products.add(new Product(2, "STRUNY", "http://quena.pl/220-large_default/daddario-ej27n-12.jpg", "DADDARIO EJ27N 1/2", 25.00, "D’Addario Classics struny uczniowskie, rozmiar 1/2 naciąg średni. Wszystkie struny wiolinowe są wyrównywane przy użyciu prawnie zastrzeżonego procesu szlifowania bezkłowego. Nieprześcigniona kontrola regularności, przekroju oraz stałej średnicy gwarantuje absolutnie perfekcyjną intonację. Rektyfikowane struny znane są ze swego „ciepłego” brzmienia."));
		products.add(new Product(3, "STRUNY", "http://quena.pl/220-large_default/daddario-ej27n-12.jpg", "DADDARIO EJ27N 1/2", 25.00, "D’Addario Classics struny uczniowskie, rozmiar 1/2 naciąg średni. Wszystkie struny wiolinowe są wyrównywane przy użyciu prawnie zastrzeżonego procesu szlifowania bezkłowego. Nieprześcigniona kontrola regularności, przekroju oraz stałej średnicy gwarantuje absolutnie perfekcyjną intonację. Rektyfikowane struny znane są ze swego „ciepłego” brzmienia."));
		products.add(new Product(4, "STRUNY", "http://quena.pl/220-large_default/daddario-ej27n-12.jpg", "DADDARIO EJ27N 1/2", 25.00, "D’Addario Classics struny uczniowskie, rozmiar 1/2 naciąg średni. Wszystkie struny wiolinowe są wyrównywane przy użyciu prawnie zastrzeżonego procesu szlifowania bezkłowego. Nieprześcigniona kontrola regularności, przekroju oraz stałej średnicy gwarantuje absolutnie perfekcyjną intonację. Rektyfikowane struny znane są ze swego „ciepłego” brzmienia."));
		products.add(new Product(5, "STRUNY", "http://quena.pl/220-large_default/daddario-ej27n-12.jpg", "DADDARIO EJ27N 1/2", 25.00, "D’Addario Classics struny uczniowskie, rozmiar 1/2 naciąg średni. Wszystkie struny wiolinowe są wyrównywane przy użyciu prawnie zastrzeżonego procesu szlifowania bezkłowego. Nieprześcigniona kontrola regularności, przekroju oraz stałej średnicy gwarantuje absolutnie perfekcyjną intonację. Rektyfikowane struny znane są ze swego „ciepłego” brzmienia."));

		return products;
	}

}
