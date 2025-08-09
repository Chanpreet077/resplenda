package com.example.resplenda.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.resplenda.model.User;
import com.example.resplenda.security.JwtUtil;
import com.example.resplenda.service.UserService;

@CrossOrigin(origins = "*")
@RestController // tells its controller
@RequestMapping("/api/users") // start of url
public class UserController {

	private final UserService userService;
	private final AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired // Spring will inject the service here
	public UserController(UserService userService, AuthenticationManager authenticationManager) {
		this.userService = userService;
		this.authenticationManager = authenticationManager;
	}

	@PostMapping("/register")
	public User registerUser(@RequestBody User user) {
		return userService.registerUser(user);
	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
		Optional<User> userOpt = userService.getUserByUsername(loginRequest.getUsername());

		if (userOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
		}

		User user = userOpt.get();

		boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
		if (!passwordMatches) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Password.");
		}

		boolean skinQuizCompleted = user.isSkinQuizCompleted();

		UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(
				loginRequest.getUsername(), loginRequest.getPassword());

		try {
			authenticationManager.authenticate(authInputToken);
		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
		}

		String token = jwtUtil.generateToken(user.getUsername());

		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		response.put("userId", user.getId());
		response.put("username", user.getUsername());
		response.put("showQuiz", !skinQuizCompleted);
		response.put("token", token);

		System.out.println("Logging in user: " + user.getUsername());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{username}")
	public Optional<User> getUserByUsername(@PathVariable String username) {
		return userService.getUserByUsername(username);
	}

	@GetMapping("/test")
	public ResponseEntity<?> testToken() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
		}

		String username = auth.getName();
		System.out.println("Authenticated user: " + username);
		return ResponseEntity.ok("Authenticated as: " + username);
	}

}