package com.ait.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.RestaurantRequestBody;
import com.ait.app.entity.Restaurant;
import com.ait.app.service.RestaurantService;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<Restaurant> saveRestaurant(
            @RequestBody RestaurantRequestBody dto) {

        Restaurant restaurant = restaurantService.saveRestaurant(dto);

        return new ResponseEntity<>(restaurant, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {

        List<Restaurant> restaurants =
                restaurantService.getAllRestaurants();

        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(
            @PathVariable Long id) {

        Restaurant restaurant =
                restaurantService.getRestaurantById(id);

        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Restaurant>> searchRestaurants(
            @RequestParam String city) {

        List<Restaurant> restaurants =
                restaurantService.getRestaurantByCity(city);

        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRestaurant(
            @PathVariable Long id) {

        restaurantService.deleteRestaurant(id);

        return new ResponseEntity<>(
                "Restaurant deleted successfully",
                HttpStatus.OK);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantRequestBody dto) {

        Restaurant restaurant =
                restaurantService.updateRestaurant(id, dto);

        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }
}