package com.example.resplenda.repository;

import org.springframework.data.jpa.repository.JpaRepository; //jpa repository helps make repository
import com.example.resplenda.model.User;

public interface UserRepository extends JpaRepository <User, Long>  { //jpa repository has pre built CRUD functions
//user is entity, Long is id data type
	User findByUsername(String Username);
}
