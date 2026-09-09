package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.RestaurantAddressRequestDto;
import com.ait.app.entity.RestaurantAddress;
import com.ait.app.service.RestaurantAddressService;

@RestController
@RequestMapping("api/restaurant-address")
public class RestaurantAddressController {
	
	@Autowired
	RestaurantAddressService restaurantAddressService;

@PostMapping
public RestaurantAddress saveRestaurantAddress(@RequestBody  RestaurantAddressRequestDto restaurantAddressRequestDto){
	
		RestaurantAddress restaurantAddress = restaurantAddressService.saveRestaurantAddress(restaurantAddressRequestDto);
		
		return restaurantAddress; 
			
}

@GetMapping("{id}")
public RestaurantAddress getRestaurantAddressById(@PathVariable Long id) {
	return restaurantAddressService.getRestaurantAddressById(id);
}

@DeleteMapping("{id}")
public String deleteRestaurantAddress(@PathVariable Long id) {
	restaurantAddressService.deleteRestaurantAddress(id);
	
	return "Restaurant address is deleted Successfully";
}
	
}
