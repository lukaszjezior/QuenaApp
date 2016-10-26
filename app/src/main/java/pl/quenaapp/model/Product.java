package pl.quenaapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

	private int productId;
	private String index;
	private String pathToPhoto;
	private String name;
	private double price;
	private String shortDescription;

	public Product(int productId, String index, String pathToPhoto,
			String name, double price, String shortDescription) {
		super();
		this.productId = productId;
		this.index = index;
		this.pathToPhoto = pathToPhoto;
		this.name = name;
		this.price = price;
		this.shortDescription = shortDescription;
	}

	public int getProductId() {
		return productId;
	}

	public String getIndex() {
		return index;
	}

	public String getPathToPhoto() {
		return pathToPhoto;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(productId);
		parcel.writeString(index);
		parcel.writeString(pathToPhoto);
		parcel.writeString(name);
		parcel.writeDouble(price);
		parcel.writeString(shortDescription);
	}

	private Product(Parcel in){
		this.productId = in.readInt();
		this.index = in.readString();
		this.pathToPhoto = in.readString();
		this.name = in.readString();
		this.price = in.readDouble();
		this.shortDescription = in.readString();
	}

	public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {

		@Override
		public Product createFromParcel(Parcel source) {
			return new Product(source);
		}

		@Override
		public Product[] newArray(int size) {
			return new Product[size];
		}
	};
}
