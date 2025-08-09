package com.example.resplenda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.resplenda.model.WishlistItem;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
	List<WishlistItem> findByUserId(Long userId);
}
