package com.ait.app.service;

import org.springframework.stereotype.Service;

import com.ait.app.dto.RestaurantAddressRequestDto;
import com.ait.app.entity.RestaurantAddress;

@Service
public interface RestaurantAddressService {

	RestaurantAddress saveRestaurantAddress(RestaurantAddressRequestDto restaurantAddressRequestDto);
	
	RestaurantAddress getRestaurantAddressById(Long id);
	
	void deleteRestaurantAddress(Long id);
	
}
