package com.example.resplenda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.resplenda.model.SkinQuiz;
import com.example.resplenda.model.User;
import com.example.resplenda.service.SkinQuizService;
import com.example.resplenda.service.UserService;

@RestController
@RequestMapping("/api/quiz")
public class SkinQuizController {

	private final SkinQuizService skinQuizService;
	private final UserService userService;

	@Autowired
	public SkinQuizController(SkinQuizService skinQuizService, UserService userService) {
		this.skinQuizService = skinQuizService;
		this.userService = userService;
	}

	@PostMapping
	public SkinQuiz saveQuiz(@RequestBody SkinQuiz quiz, @RequestParam Long userId) {
		User user = userService.getUserById(userId); // fetch full user
		quiz.setUser(user); // set user in quiz

		SkinQuiz savedQuiz = skinQuizService.saveOrUpdate(quiz);

		if (!user.isSkinQuizCompleted()) {
			user.setIsSkinQuizCompleted(true);
			userService.saveUser(user);
		}

		return savedQuiz;
	}

	@GetMapping("/{userId}")
	public SkinQuiz getQuizByUserId(@PathVariable Long userId) {
		return skinQuizService.getByUserId(userId);

	}
}
