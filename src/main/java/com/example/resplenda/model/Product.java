package com.example.resplenda.model;

public class Product {
private String name;
private String brand;
private String productType;
private String imageLink;
private String description;
private String productLink;


public Product() { //empty constructor for data base
	
}

public Product(String name, String brand, String productType, String imageLink, String description, String productLink ) {
this.name = name;
this.brand = brand;  //filled constructor for testing
this.productType = productType;
this.imageLink = imageLink;
this.description = description;
this.productLink = productLink;
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
	

}
