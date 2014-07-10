package com.cryingonion.instashop.model;

public class CategoryDetail {

	private String categoryName;
	private String categoryTag;
	private String categoryPath;
	
	public CategoryDetail() {}
	
	public CategoryDetail(String categoryName, String categoryTag, String categoryPath) {
		this.categoryName = categoryName;
		this.categoryTag = categoryTag;
		this.categoryPath = categoryPath;
	}
	
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	public String getCategoryName() {
		return categoryName;
	}
	
	public void setCategoryTag(String categoryTag) {
		this.categoryTag = categoryTag;
	}
	
	public String getCategoryTag() {
		return categoryTag;
	}
	
	public void setCategoryPath(String categoryPath) {
		this.categoryPath = categoryPath;
	}
	
	public String getCategoryPath() {
		return categoryPath;
	}
	
	
	
}
