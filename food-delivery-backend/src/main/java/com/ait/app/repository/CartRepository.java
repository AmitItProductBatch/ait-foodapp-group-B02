package com.ait.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer>{

}
