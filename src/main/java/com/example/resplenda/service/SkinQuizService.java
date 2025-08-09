package com.example.resplenda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.resplenda.model.SkinQuiz;
import com.example.resplenda.repository.SkinQuizRepository;

@Service
public class SkinQuizService {

	private final SkinQuizRepository skinQuizRepository;

	@Autowired
	public SkinQuizService(SkinQuizRepository skinQuizRepository) {
		this.skinQuizRepository = skinQuizRepository;
	}

	public SkinQuiz saveOrUpdate(SkinQuiz quiz) {
		return skinQuizRepository.save(quiz);
	}

	public SkinQuiz getByUserId(Long userId) {
		return skinQuizRepository.findByUserId(userId);
	}

}
