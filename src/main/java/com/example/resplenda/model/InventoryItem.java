package com.example.resplenda.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Store product details directly
	private String productName;
	private String productBrand;
	private String productImageLink;
	private String productType;
	private String productCategory;
	private String productLink;

	@ElementCollection
	private List<String> productTags;

	@Column(length = 1000)
	private String description;

	// User who owns this inventory item
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	// Dates for expiry tracking
	private LocalDate openDate;
	private LocalDate expiryDate;

	// Constructors
	public InventoryItem() {
	}

	public InventoryItem(User user, String productName, String productBrand, String productImageLink,
			String productType, String productCategory, String productLink, List<String> productTags,
			String description, LocalDate openDate, LocalDate expiryDate) {
		this.user = user;
		this.productName = productName;
		this.productBrand = productBrand;
		this.productImageLink = productImageLink;
		this.productType = productType;
		this.productCategory = productCategory;
		this.productLink = productLink;
		this.productTags = productTags;
		this.description = description;
		this.openDate = openDate;
		this.expiryDate = expiryDate;
	}

	// Getters & Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
	}

	public String getProductImageLink() {
		return productImageLink;
	}

	public void setProductImageUrl(String productImageLink) {
		this.productImageLink = productImageLink;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getProductLink() {
		return productLink;
	}

	public void setProductLink(String productLink) {
		this.productLink = productLink;
	}

	public void setProductTags(List<String> productTags) {
		this.productTags = productTags;
	}

	public List<String> getProductTags() {
		// TODO Auto-generated method stub
		return productTags;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDate getOpenDate() {
		return openDate;
	}

	public void setOpenDate(LocalDate openDate) {
		this.openDate = openDate;
	}

	public LocalDate getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}
}
