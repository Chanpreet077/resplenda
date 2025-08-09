package com.example.resplenda.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.resplenda.model.Product;
import com.example.resplenda.model.SkinQuiz;
import com.example.resplenda.service.MakeupApiService;
import com.example.resplenda.service.SkinQuizService;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

	private final MakeupApiService makeupApiService;
	private final SkinQuizService skinQuizService;

	@Autowired
	public RecommendationController(MakeupApiService makeupApiService, SkinQuizService skinQuizService) {
		this.makeupApiService = makeupApiService;
		this.skinQuizService = skinQuizService;
	}

	@GetMapping("/{userId}")
	public ResponseEntity<List<Product>> getRecommendations(@PathVariable Long userId) {
		SkinQuiz quiz = skinQuizService.getByUserId(userId);
		if (quiz == null) {
			return ResponseEntity.notFound().build();
		}

		List<Product> recommendedProducts = makeupApiService.recommendProducts(new ArrayList<>(quiz.getProductPref()),
				quiz.getSkinType(), quiz.getFinish(), new ArrayList<>(quiz.getTagPref())

		);

		return ResponseEntity.ok(recommendedProducts);

	}

}
