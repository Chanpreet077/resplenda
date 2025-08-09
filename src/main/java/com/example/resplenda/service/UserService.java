package com.example.resplenda.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.resplenda.model.User;
import com.example.resplenda.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired // makes spring give an object of the class/interface so its used here
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) { // constructor for service
																							// class
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public Optional<User> getUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public User getUserById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}

	public User registerUser(User user) {
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new RuntimeException("This email already exists.");
		}
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			throw new RuntimeException("This username is already in use.");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword())); // changes current pw to encoded one for saving
		return saveUser(user);

	}

}
