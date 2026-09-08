package com.ait.app.service;

import org.springframework.stereotype.Service;

import com.ait.app.dto.RestaurantRequestBody;
import com.ait.app.entity.Restaurant;

@Service
public interface RestaurantService {

	Restaurant saveRestaurant(RestaurantRequestBody restaurantRequestBody);
	
	Restaurant getRestaurantById(Long id);
	
	void deleteRestaurant(Long id);
	
}
