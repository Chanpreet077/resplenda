package com.example.resplenda.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class User {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String email;
private String username;
private String password;

	public User() {//no args for database to actually use
		
	}

	public User( Long id, String email,  String username, String password) { //parameters for testing purps / dummy data
		this.id = id;
		this.email = email;
		this.username = username;
		this.password = password;
	}
	

	public Long getId() {
		return id;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
}
