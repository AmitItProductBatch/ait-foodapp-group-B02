package com.ait.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer>{

	boolean existsByUserId(int userId);
	Optional<Cart> findByUserId(int userId);
}
