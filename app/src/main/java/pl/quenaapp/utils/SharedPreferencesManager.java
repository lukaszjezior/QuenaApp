package pl.quenaapp.utils;

import android.content.SharedPreferences;

public class SharedPreferencesManager {

	private SharedPreferences shPref;
	private String TAG;

	// Ka¿da aktywnoœæ która wczytuje dane z internetu posiada swoj¹ zmienn¹,
	// któr¹ sprawdza przed wczytaniem danych.
	// Zmienna ma byæ ustawiona na 1 kiedy znajdujemy siê na danym ekranie, aby
	// mo¿na go by³o obracaæ
	// Jeœli w jakikolwiek sposób opuszczamy aktywnoœæ, zmienna powinna zostaæ
	// wyzerowana - wtedy wiadomo, ¿e dane
	// nie s¹ wczytane i nale¿y je wczytaæ przed utworzeniem listy.

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
