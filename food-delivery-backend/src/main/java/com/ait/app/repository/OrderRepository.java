package com.ait.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer>{

    List<Order> findByUserId(int userId);

    List<Order> findByRestaurantId(Long restaurantId);
}