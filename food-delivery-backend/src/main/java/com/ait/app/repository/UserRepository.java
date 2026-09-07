package com.ait.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{

	boolean existsByEmail(String email);
	boolean existsByMobile(long mobile);
}
