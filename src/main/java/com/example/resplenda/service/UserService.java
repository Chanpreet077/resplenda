package com.example.resplenda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.resplenda.model.User;

import com.example.resplenda.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
	
	@Autowired //makes spring give an object of the class/interface so its used here
	public UserService(UserRepository userRepository) { //constructor for service class
		this.userRepository = userRepository;
	}
	
	
	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	public User saveUser(User user) {
		return userRepository.save(user);
	}

}
