package com.example.resplenda.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.resplenda.model.Product;

import jakarta.annotation.PostConstruct;

@Service
public class MakeupApiService {

	private final RestTemplate restTemplate;

	private final String base_url = "http://makeup-api.herokuapp.com/api/v1/products.json";

	private static final Map<String, List<String>> skinTypeToTags = Map.of("oily", List.of("oil free"), "dry",
			List.of("hydrating", "moisturizing"), "combination", List.of("balanced", "non-greasy"), "sensitive",
			List.of("hypoallergenic", "fragrance free"), "normal", List.of("all skin types")
	// Add more skin types and their associated tags here as needed
	);

	private static final Map<String, List<String>> finishToCategories = Map
			.of("matte",
					List.of("powder", "matte", "pressed powder", "matte lipstick", "matte blush", "matte foundation",
							"matte bronzer"),
					"dewy", List.of("liquid", "cream", "glossy", "highlighter", "dewy", "gloss"));

	@Autowired
	public MakeupApiService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private List<Product> cachedProducts = new ArrayList<>();

	public List<Product> fetchProducts() {

		Product[] products = restTemplate.getForObject(base_url, Product[].class);// product[].class means "array of
																					// Product objects"
		return Arrays.asList(products);
	}

	@PostConstruct
	public void initCache() {
		refreshCache();
	}

	private void refreshCache() {
		cachedProducts = fetchProducts();

	}

	public List<Product> getAllCachedProducts() {
		return cachedProducts;
	}

	public List<Product> searchProduct(String searchTerm) {
		if (searchTerm == null || searchTerm.isBlank()) {
			return new ArrayList<>();
		}
		String lowerTerm = searchTerm.toLowerCase();

		return cachedProducts.stream().filter(p -> {
			String name = p.getName() != null ? p.getName().toLowerCase() : "";
			String type = p.getProductType() != null ? p.getProductType().toLowerCase() : "";

			// Matches if search term is contained in name or type
			return name.contains(lowerTerm) || type.contains(lowerTerm);
		}).collect(Collectors.toList());
	}

	public List<Product> getProductByProductType(String productType) {
		String url = base_url + "?product_category=" + productType;
		Product[] products = restTemplate.getForObject(url, Product[].class);
		return products == null ? Collections.emptyList() : Arrays.asList(products);
	}

	public List<Product> getProductsByProductTypes(List<String> categories) {
		List<Product> allProducts = new ArrayList<>();
		for (String category : categories) {
			allProducts.addAll(getProductByCategory(category));
		}
		return allProducts;
	}

	public List<Product> getProductByCategory(String category) {
		String url = base_url + "?product_category=" + category;
		Product[] products = restTemplate.getForObject(url, Product[].class);
		return products == null ? Collections.emptyList() : Arrays.asList(products);
	}

	public List<Product> getProductsByCategories(List<String> categories) {
		List<Product> allProducts = new ArrayList<>();
		for (String category : categories) {
			allProducts.addAll(getProductByCategory(category));
		}
		return allProducts;
	}

	private List<Product> filterByCategories(List<Product> products, List<String> categories) {
		if (categories == null || categories.isEmpty())
			return products;

		Set<String> lowerCategories = categories.stream().map(String::toLowerCase).collect(Collectors.toSet());

		return products.stream().filter(p -> {
			if ((p.getProductType() != null && lowerCategories.contains(p.getProductType().toLowerCase()))
					|| (p.getProductCategory() != null
							&& lowerCategories.contains(p.getProductCategory().toLowerCase()))) {
				return true;
			}
			return false;
		}).collect(Collectors.toList());
	}

	private List<Product> filterByTags(List<Product> products, List<String> requiredTags) {
		if (requiredTags == null || requiredTags.isEmpty())
			return products;

		Set<String> requiredLowerTags = requiredTags.stream().map((String tag) -> tag.toLowerCase())
				.collect(Collectors.toSet());

		return products.stream().filter(p -> {
			if (p.getProductTags() == null)
				return false;
			Set<String> productTagsLower = p.getProductTags().stream().map(String::toLowerCase)
					.collect(Collectors.toSet());
			return requiredLowerTags.stream().anyMatch(productTagsLower::contains);

		}).collect(Collectors.toList());
	}

	private List<Product> mergeUnique(List<Product> list1, List<Product> list2) {
		LinkedHashSet<Product> set = new LinkedHashSet<>();
		set.addAll(list1);
		set.addAll(list2);
		return new ArrayList<>(set);
	}

	public List<Product> recommendProducts(List<String> productTypes, String skinType, String finish,
			List<String> extraTags) {

		// Normalize input to lowercase to avoid case mismatches
		String normalizedSkinType = skinType == null ? "" : skinType.toLowerCase();
		String normalizedFinish = finish == null ? "" : finish.toLowerCase();
		List<String> normalizedExtraTags = extraTags == null ? List.of()
				: extraTags.stream().map(String::toLowerCase).toList();

		// Fetch all products for the selected product types
		List<Product> products = getProductsByCategories(productTypes);

		// Get associated tags/categories for skin type and finish
		List<String> skinTags = skinTypeToTags.getOrDefault(normalizedSkinType, List.of());
		List<String> finishCats = finishToCategories.getOrDefault(normalizedFinish, List.of());

		// Step 1: Strict filter: must match finish categories + skin tags + extra tags
		List<Product> strictFiltered = filterByCategories(products, finishCats);
		strictFiltered = filterByTags(strictFiltered, skinTags);
		strictFiltered = filterByTags(strictFiltered, normalizedExtraTags);

		if (strictFiltered.size() >= 30) {
			return strictFiltered.stream().limit(30).toList();
		}

		// Step 2: Relax extra tags (only finish categories + skin tags)
		List<Product> relaxedExtraTags = filterByCategories(products, finishCats);
		relaxedExtraTags = filterByTags(relaxedExtraTags, skinTags);

		List<Product> combined = mergeUnique(strictFiltered, relaxedExtraTags);
		if (combined.size() >= 30) {
			return combined.stream().limit(30).toList();
		}

		// Step 3: Relax skin tags (only finish categories)
		List<Product> relaxedSkinTags = filterByCategories(products, finishCats);

		combined = mergeUnique(combined, relaxedSkinTags);
		if (combined.size() >= 30) {
			return combined.stream().limit(30).toList();
		}

		// Step 4: Relax finish categories (just product types filter, no finish/skin
		// tags)
		combined = mergeUnique(combined, products);
		if (combined.size() >= 30) {
			return combined.stream().limit(30).toList();
		}

		// Step 5: Return whatever products collected (even if less than 30)
		return combined;
	}

	public List<Product> getProductsByName(String name) {
		List<Product> allProducts = fetchProducts();
		return allProducts.stream()
				.filter(p -> p.getName() != null && p.getName().toLowerCase().contains(name.toLowerCase()))
				.collect(Collectors.toList());
	}
}
