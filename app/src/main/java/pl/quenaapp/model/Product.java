package pl.quenaapp.model;

public class Product {

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

}
