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

import com.example.resplenda.dto.AddWishlistRequest;
import com.example.resplenda.model.User;
import com.example.resplenda.model.WishlistItem;
import com.example.resplenda.service.UserService;
import com.example.resplenda.service.WishlistItemService;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistItemController {

	@Autowired
	private WishlistItemService wishlistService;

	@Autowired
	private UserService userService;

	@PostMapping("/add")
	public ResponseEntity<?> addWishlistItem(@RequestBody AddWishlistRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
		}

		String username = auth.getName();
		User user = userService.getUserByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		try {
			WishlistItem savedItem = wishlistService.addWishlistItemByProductName(user.getId(),
					request.getProductName());
			return ResponseEntity.ok(savedItem);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/all")
	public ResponseEntity<?> getUserWishlist() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
		}

		String username = auth.getName();
		User user = userService.getUserByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		List<WishlistItem> items = wishlistService.getUserWishlist(user.getId());
		return ResponseEntity.ok(items);
	}

	@DeleteMapping("/delete/{itemId}")
	public ResponseEntity<?> deleteWishlistItem(@PathVariable Long itemId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
		}

		String username = auth.getName();
		User user = userService.getUserByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		boolean ownsItem = wishlistService.getUserWishlist(user.getId()).stream()
				.anyMatch(item -> item.getId().equals(itemId));

		if (!ownsItem) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not own this item");
		}

		wishlistService.deleteWishlistItem(itemId);
		return ResponseEntity.ok("Deleted item with id " + itemId);
	}
}
