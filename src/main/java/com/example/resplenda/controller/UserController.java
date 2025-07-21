package com.example.resplenda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.resplenda.model.User;
import com.example.resplenda.service.UserService;

@RestController //tells its controller
@RequestMapping("/api/users") //start of url
public class UserController {

    private final UserService userService;

    @Autowired // Spring will inject the service here
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.saveUser(user);
    }
    
    @GetMapping("/{username}")
    public User getUserByUsername(@PathVariable String username) {
    	return userService.getUserByUsername(username);
    }
    
    
    @GetMapping("/test")
    public String test() {
        return "API is working!";
    }
}