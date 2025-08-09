package com.example.resplenda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.resplenda.model.SkinQuiz;

public interface SkinQuizRepository extends JpaRepository<SkinQuiz, Long> {
	SkinQuiz findByUserId(Long userId);
}
