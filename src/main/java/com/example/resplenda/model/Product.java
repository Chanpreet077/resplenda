package com.example.resplenda.model;

import java.util.List;

public class Product {
	private String name;
	private String brand;
	private String productType;
	private String productCategory;
	private String imageLink;
	private String description;
	private String productLink;
	private List<String> productTags;

	public Product() { // empty constructor for data base

	}

	public Product(String name, String brand, String productType, String imageLink, String description,
			String productLink, String productCategory, List<String> productTags) {
		this.name = name;
		this.brand = brand; // filled constructor for testing
		this.productType = productType;
		this.imageLink = imageLink;
		this.description = description;
		this.productLink = productLink;
		this.productCategory = productCategory;
		this.productTags = productTags;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getImageLink() {
		return imageLink;
	}

	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProductLink() {
		return productLink;
	}

	public void setProductLink(String productLink) {
		this.productLink = productLink;
	}

	public String getProductCategory() {
		// TODO Auto-generated method stub
		return productCategory;
	}

	public void setProductTags(List<String> productTags) {
		this.productTags = productTags;
	}

	public List<String> getProductTags() {
		// TODO Auto-generated method stub
		return productTags;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

}
