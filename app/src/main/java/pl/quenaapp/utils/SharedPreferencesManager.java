package pl.quenaapp.utils;

import android.content.SharedPreferences;

public class SharedPreferencesManager {

	private SharedPreferences shPref;
	private String TAG;

	// Ka�da aktywno�� kt�ra wczytuje dane z internetu posiada swoj� zmienn�,
	// kt�r� sprawdza przed wczytaniem danych.
	// Zmienna ma by� ustawiona na 1 kiedy znajdujemy si� na danym ekranie, aby
	// mo�na go by�o obraca�
	// Je�li w jakikolwiek spos�b opuszczamy aktywno��, zmienna powinna zosta�
	// wyzerowana - wtedy wiadomo, �e dane
	// nie s� wczytane i nale�y je wczyta� przed utworzeniem listy.

	public SharedPreferencesManager(SharedPreferences shPref) {
		this.shPref = shPref;
	}

	public Boolean getNewsLoadedVar() {
		return shPref.getBoolean("newsLoadedVar", false);
	}

	public void resetNewsLoadedVar() {
		shPref.edit().putBoolean("newsLoadedVar", false).commit();
	}

	public void setNewsLoadedVar() {
		shPref.edit().putBoolean("newsLoadedVar", true).commit();
	}

	public Boolean getCategoriesLoadedVar() {
		return shPref.getBoolean("categoryLoadedVar", false);
	}

	public void resetCategoriesLoadedVar() {
		shPref.edit().putBoolean("categoryLoadedVar", false).commit();
	}

	public void setCategoriesLoadedVar() {
		shPref.edit().putBoolean("categoryLoadedVar", true).commit();
	}

	public Boolean getProductsLoadedVar() {
		return shPref.getBoolean("productsLoadedVar", false);
	}

	public void resetProductsLoadedVar() {
		shPref.edit().putBoolean("productsLoadedVar", false).commit();
	}

	public void setProductsLoadedVar() {
		shPref.edit().putBoolean("productsLoadedVar", true).commit();
	}

	public Boolean getPhotoLoadedVar() {
		return shPref.getBoolean("productPhotoLoadedVar", false);
	}

	public void resetPhotoLoadedVar() {
		shPref.edit().putBoolean("productPhotoLoadedVar", false).commit();
	}

	public void setPhotoLoadedVar() {
		shPref.edit().putBoolean("productPhotoLoadedVar", true).commit();
	}

	public void resetAllVariables() {
		resetPhotoLoadedVar();
		resetProductsLoadedVar();
		resetCategoriesLoadedVar();
		resetNewsLoadedVar();
	}
}
