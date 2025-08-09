package com.example.resplenda.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.resplenda.model.InventoryItem;
import com.example.resplenda.model.Product;
import com.example.resplenda.model.User;
import com.example.resplenda.repository.InventoryItemRepository;
import com.example.resplenda.repository.UserRepository;

@Service
public class InventoryItemService {

	@Autowired
	private InventoryItemRepository inventoryItemRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MakeupApiService makeupApiService;

	// Add inventory item by product name (search product, convert, save)
	public InventoryItem addInventoryItemByProductName(Long userId, String productName, LocalDate openDate,
			LocalDate expiryDate) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		// Search product from makeup API service by name (assume exact or partial
		// match)
		List<Product> products = makeupApiService.getProductsByName(productName);
		if (products.isEmpty()) {
			throw new RuntimeException("Product not found with name: " + productName);
		}

		Product product = products.get(0);

		// Convert Product to InventoryItem
		InventoryItem item = new InventoryItem(user, product.getName(), product.getBrand(), product.getImageLink(),
				product.getProductType(), product.getProductCategory(), product.getProductLink(),
				product.getProductTags(), product.getDescription(), openDate, expiryDate);

		return inventoryItemRepository.save(item);
	}

	// Get all inventory items for a user
	public List<InventoryItem> getUserInventory(Long userId) {
		return inventoryItemRepository.findByUserId(userId);
	}

	// Delete inventory item by id
	public void deleteInventoryItem(Long itemId) {
		inventoryItemRepository.deleteById(itemId);
	}
}
