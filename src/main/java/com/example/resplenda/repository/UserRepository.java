package com.example.resplenda.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository; //jpa repository helps make repository

import com.example.resplenda.model.User;

public interface UserRepository extends JpaRepository<User, Long> { // jpa repository has pre built CRUD functions
//user is entity, Long is id data type
	Optional<User> findByUsername(String Username); // optional allows it to not give null error if a user isnt found
													// with that name

	Optional<User> findByEmail(String email);
}
