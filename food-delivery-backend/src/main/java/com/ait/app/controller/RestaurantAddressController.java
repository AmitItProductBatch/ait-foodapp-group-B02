package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.RestaurantAddressRequestDto;
import com.ait.app.entity.RestaurantAddress;
import com.ait.app.service.RestaurantAddressService;

@RestController
public class RestaurantAddressController {
	
	@Autowired
	RestaurantAddressService restaurantAddressService;

@PostMapping("api/restaurant/address")
public RestaurantAddress saveRestaurantAddress(@RequestBody  RestaurantAddressRequestDto restaurantAddressRequestDto){
	
		RestaurantAddress restaurantAddress = restaurantAddressService.saveRestaurantAddress(restaurantAddressRequestDto);
		
		return restaurantAddress; 
			
}

@GetMapping("api/restaurant/{id}")
public RestaurantAddress getRestaurantAddressById(@PathVariable Long id) {
	return restaurantAddressService.getRestaurantAddressById(id);
}

@DeleteMapping("api/restaurant/{id}")
public String deleteRestaurantAddress(@PathVariable Long id) {
	restaurantAddressService.deleteRestaurantAddress(id);
	
	return "Restaurant address is deleted Successfully";
}
	
}
