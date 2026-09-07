package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.RestaurantRequestBody;
import com.ait.app.model.Restaurant;
import com.ait.app.service.RestaurantService;

@RestController
public class RestaurantController {

	@Autowired
	RestaurantService restaurantService;
	
	
	@PostMapping("api/restaurants")
	public ResponseEntity<Restaurant> saveRestaurant(@RequestBody RestaurantRequestBody restaurantRequestBody){
		
		Restaurant restaurant = restaurantService.saveRestaurant(restaurantRequestBody);
		
		return new ResponseEntity(restaurant, HttpStatus.CREATED);
		
	}
	
	@GetMapping("api/restaurants/{id}")
	public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id){
		
		Restaurant restaurant = restaurantService.getRestaurantById(id);
		
		return new ResponseEntity(restaurant, HttpStatus.OK);
	}
	
	@DeleteMapping("api/restaurants/{id}")
	public String deleteRestaurantById(@PathVariable Long id){
		restaurantService.deleteRestaurant(id);
		return "Restaurant is deleted Succesffully";
	}
	
}
