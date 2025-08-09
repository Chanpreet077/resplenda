package com.example.resplenda.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.resplenda.model.Product;
import com.example.resplenda.service.MakeupApiService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final MakeupApiService makeupApiService;

	@Autowired
	public ProductController(MakeupApiService makeupApiService) {
		this.makeupApiService = makeupApiService;
	}

	@GetMapping
	public ResponseEntity<List<Product>> getFilteredProductsProductType(
			@RequestParam(required = false) String productType, @RequestParam(required = false) String name) {
		List<Product> products = makeupApiService.fetchProducts();

		if (productType != null) {
			products = makeupApiService.getProductByProductType(productType);
		} else if (name != null) {
			products = makeupApiService.getProductsByName(name);
		}

		return ResponseEntity.ok(products);
	}

	@GetMapping("/search")
	public ResponseEntity<List<Product>> searchProducts(@RequestParam(required = false) String query) {
		if (query == null || query.trim().isEmpty()) {
			// Return an empty list or default recommendations
			return ResponseEntity.ok(new ArrayList<>());
		}

		List<Product> products = makeupApiService.searchProduct(query);
		return ResponseEntity.ok(products);
	}

}
