package com.ait.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.ait.app.entity.Restaurant;


public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
	
	List<Restaurant> findByRestaurantAddressCity(String city);

}
