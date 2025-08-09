package com.example.resplenda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.resplenda.model.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
	List<InventoryItem> findByUserId(Long userId);
}
