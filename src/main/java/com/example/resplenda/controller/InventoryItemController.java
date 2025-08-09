package com.example.resplenda.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.resplenda.dto.AddInventoryRequest;
import com.example.resplenda.model.InventoryItem;
import com.example.resplenda.model.User;
import com.example.resplenda.service.InventoryItemService;
import com.example.resplenda.service.UserService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryItemController {

	@Autowired
	private InventoryItemService inventoryService;

	@Autowired
	private UserService userService;

	// Add inventory item (by product name)
	@PostMapping("/add")
	public ResponseEntity<?> addInventoryItem(@RequestBody AddInventoryRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
		}

		String username = auth.getName();

		User user = userService.getUserByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		try {
			InventoryItem savedItem = inventoryService.addInventoryItemByProductName(user.getId(),
					request.getProductName(), request.getOpenDate(), request.getExpiryDate() // logic for
																								// expiry*****************
			);
			return ResponseEntity.ok(savedItem);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// Get all inventory items for logged-in user
	@GetMapping("/all")
	public ResponseEntity<?> getUserInventory() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
		}

		String username = auth.getName();

		User user = userService.getUserByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		List<InventoryItem> items = inventoryService.getUserInventory(user.getId());
		return ResponseEntity.ok(items);
	}

	// Delete inventory item by id, only if belongs to user
	@DeleteMapping("/delete/{itemId}")
	public ResponseEntity<?> deleteInventoryItem(@PathVariable Long itemId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
		}

		String username = auth.getName();

		User user = userService.getUserByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		// Verify ownership before deleting
		List<InventoryItem> userItems = inventoryService.getUserInventory(user.getId());
		boolean ownsItem = userItems.stream().anyMatch(item -> item.getId().equals(itemId));

		if (!ownsItem) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not own this item");
		}

		inventoryService.deleteInventoryItem(itemId);
		return ResponseEntity.ok("Deleted item with id " + itemId);
	}
}
