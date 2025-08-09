package com.example.resplenda.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.resplenda.model.Product;
import com.example.resplenda.model.User;
import com.example.resplenda.model.WishlistItem;
import com.example.resplenda.repository.UserRepository;
import com.example.resplenda.repository.WishlistItemRepository;

@Service
public class WishlistItemService {

	@Autowired
	private WishlistItemRepository wishlistItemRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MakeupApiService makeupApiService;

	public WishlistItem addWishlistItemByProductName(Long userId, String productName) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		// Prevent duplicates
		boolean exists = wishlistItemRepository.findByUserId(userId).stream()
				.anyMatch(item -> item.getProductName().equalsIgnoreCase(productName));
		if (exists) {
			throw new RuntimeException("Item already in wishlist");
		}

		List<Product> products = makeupApiService.getProductsByName(productName);
		if (products.isEmpty()) {
			throw new RuntimeException("Product not found with name: " + productName);
		}

		Product product = products.get(0);

		WishlistItem item = new WishlistItem(user, product.getName(), product.getBrand(), product.getImageLink(),
				product.getProductType(), product.getProductCategory(), product.getProductLink(),
				product.getProductTags(), product.getDescription());

		return wishlistItemRepository.save(item);
	}

	public List<WishlistItem> getUserWishlist(Long userId) {
		return wishlistItemRepository.findByUserId(userId);
	}

	public void deleteWishlistItem(Long itemId) {
		wishlistItemRepository.deleteById(itemId);
	}
}
