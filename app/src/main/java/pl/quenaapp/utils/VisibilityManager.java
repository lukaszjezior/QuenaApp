package pl.quenaapp.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class VisibilityManager {

	// methods for showing product list, categories list etc.

	public void showProductsListView(LinearLayout noProductsLayout,
			LinearLayout progressLayout, LinearLayout noInternetLayout,
			ListView productListView) {

		makeAllGone(noProductsLayout, progressLayout, noInternetLayout,
				productListView);
		productListView.setVisibility(View.VISIBLE);
	}

	public void showCategoriesListView(LinearLayout progressLayout,
			LinearLayout noInternetLayout, ListView categoriesListView) {

		makeAllGone(progressLayout, noInternetLayout, categoriesListView);
		categoriesListView.setVisibility(View.VISIBLE);
	}

	public void showPhotoImageView(LinearLayout noPhotoLayout,
			LinearLayout progressLayout, LinearLayout noInternetLayout,
			ImageView photoImageView) {

		makeAllGone(noPhotoLayout, progressLayout, noInternetLayout,
				photoImageView);
		photoImageView.setVisibility(View.VISIBLE);
	}

	// methods for showing no products, photo etc.

	public void showNoProductsLayout(LinearLayout noProductsLayout,
			LinearLayout progressLayout, LinearLayout noInternetLayout,
			ListView productListView) {

		makeAllGone(noProductsLayout, progressLayout, noInternetLayout,
				productListView);
		noProductsLayout.setVisibility(View.VISIBLE);
	}

	public void showNoPhotoLayout(LinearLayout noPhotoLayout,
			LinearLayout progressLayout, LinearLayout noInternetLayout,
			ImageView photoImageView) {

		makeAllGone(noPhotoLayout, progressLayout, noInternetLayout,
				photoImageView);
		noPhotoLayout.setVisibility(View.VISIBLE);
	}

	// overloaded methods for showing loading layout

	public void showLoadingLayout(LinearLayout noProductsLayout,
			LinearLayout progressLayout, LinearLayout noInternetLayout,
			ListView productListView) {

		makeAllGone(noProductsLayout, progressLayout, noInternetLayout,
				productListView);
		progressLayout.setVisibility(View.VISIBLE);
	}

	public void showLoadingLayout(LinearLayout progressLayout,
			LinearLayout noInternetLayout, ListView productListView) {

		makeAllGone(progressLayout, noInternetLayout, productListView);
		progressLayout.setVisibility(View.VISIBLE);
	}

	public void showLoadingLayout(LinearLayout noPhotoLayout,
			LinearLayout progressLayout, LinearLayout noInternetLayout,
			ImageView photoImageView) {

		makeAllGone(noPhotoLayout, progressLayout, noInternetLayout,
				photoImageView);
		progressLayout.setVisibility(View.VISIBLE);
	}

	// overloaded methods for showing no internet connection

	public void showNoInternetLayout(LinearLayout noProductsLayout,
			LinearLayout progressLayout, LinearLayout noInternetLayout,
			ListView productListView) {

		makeAllGone(noProductsLayout, progressLayout, noInternetLayout,
				productListView);
		noInternetLayout.setVisibility(View.VISIBLE);
	}

	public void showNoInternetLayout(LinearLayout progressLayout,
			LinearLayout noInternetLayout, ListView productListView) {

		makeAllGone(progressLayout, noInternetLayout, productListView);
		noInternetLayout.setVisibility(View.VISIBLE);
	}

	public void showNoInternetLayout(LinearLayout noPhotoLayout,
			LinearLayout progressLayout, LinearLayout noInternetLayout,
			ImageView photoImageView) {
		makeAllGone(noPhotoLayout, progressLayout, noInternetLayout,
				photoImageView);
		noInternetLayout.setVisibility(View.VISIBLE);
	}

	// overloaded methods for making all layouts gone

	private void makeAllGone(LinearLayout noProductsLayout,
			LinearLayout progressLayout, LinearLayout noInternetLayout,
			ListView productListView) {

		noProductsLayout.setVisibility(View.GONE);
		progressLayout.setVisibility(View.GONE);
		noInternetLayout.setVisibility(View.GONE);
		productListView.setVisibility(View.GONE);
	}

	private void makeAllGone(LinearLayout progressLayout,
			LinearLayout noInternetLayout, ListView categoriesListView) {

		progressLayout.setVisibility(View.GONE);
		noInternetLayout.setVisibility(View.GONE);
		categoriesListView.setVisibility(View.GONE);
	}

	private void makeAllGone(LinearLayout noPhotoLayout,
			LinearLayout progressLayout, LinearLayout noInternetLayout,
			ImageView photoImageView) {

		noPhotoLayout.setVisibility(View.GONE);
		progressLayout.setVisibility(View.GONE);
		noInternetLayout.setVisibility(View.GONE);
		photoImageView.setVisibility(View.GONE);
	}

}
