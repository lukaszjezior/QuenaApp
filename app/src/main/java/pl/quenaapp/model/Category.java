package pl.quenaapp.model;

public class Category {
	
	private int categoryNumber;
	private int parentNumber;
	private String title;
	private int childsCount;
	
	public Category(int categoryNumber, int parentNumber, String title,
			int childsCount) {
		super();
		this.categoryNumber = categoryNumber;
		this.parentNumber = parentNumber;
		this.title = title;
		this.childsCount = childsCount;
	}

	public int getCategoryNumber() {
		return categoryNumber;
	}

	public void setCategoryNumber(int categoryNumber) {
		this.categoryNumber = categoryNumber;
	}

	public int getParentNumber() {
		return parentNumber;
	}

	public void setParentNumber(int parentNumber) {
		this.parentNumber = parentNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getChildsCount() {
		return childsCount;
	}

	public void setChildsCount(int childsCount) {
		this.childsCount = childsCount;
	}
	
}
