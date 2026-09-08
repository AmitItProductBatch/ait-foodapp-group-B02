package com.ait.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.FoodItem;

public interface FooditemRepo extends JpaRepository<FoodItem, Integer>{

	Optional<FoodItem> findByFoodname(String foodname);
}
