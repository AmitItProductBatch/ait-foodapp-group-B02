package com.ait.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ait.app.dto.RestaurantRequestBody;
import com.ait.app.entity.Restaurant;

@Service
public interface RestaurantService {

	Restaurant saveRestaurant(RestaurantRequestBody restaurantRequestBody);
	
	Restaurant getRestaurantById(Long id);
	
	List<Restaurant> getRestaurantByCity(String city);
	
	void deleteRestaurant(Long id);
	
}
