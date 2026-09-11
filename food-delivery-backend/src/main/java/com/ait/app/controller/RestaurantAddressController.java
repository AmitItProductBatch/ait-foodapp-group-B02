package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/restaurant-address")
public class RestaurantAddressController {

    @Autowired
    RestaurantAddressService restaurantAddressService;

    @PostMapping
    public ResponseEntity<RestaurantAddress> saveRestaurantAddress(
            @RequestBody RestaurantAddressRequestDto restaurantAddressRequestDto) {

        RestaurantAddress restaurantAddress =
                restaurantAddressService.saveRestaurantAddress(
                        restaurantAddressRequestDto);

        return new ResponseEntity<>(
                restaurantAddress,
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantAddress> getRestaurantAddressById(
            @PathVariable Long id) {

        RestaurantAddress restaurantAddress =
                restaurantAddressService.getRestaurantAddressById(id);

        return new ResponseEntity<>(
                restaurantAddress,
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRestaurantAddress(
            @PathVariable Long id) {

        restaurantAddressService.deleteRestaurantAddress(id);

        return new ResponseEntity<>(
                "Restaurant address is deleted successfully",
                HttpStatus.OK);
    }
}