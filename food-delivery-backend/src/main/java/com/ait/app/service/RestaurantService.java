package com.ait.app.service;

import java.util.List;

import com.ait.app.dto.RestaurantRequestBody;
import com.ait.app.entity.Restaurant;

public interface RestaurantService {

    
    Restaurant saveRestaurant(RestaurantRequestBody restaurantRequestBody);

    
    List<Restaurant> getAllRestaurants();

    
    Restaurant getRestaurantById(Long id);
    
    Restaurant updateRestaurant( Long id, RestaurantRequestBody restaurantRequestBody);

    
    List<Restaurant> getRestaurantByCity(String city);

    
    void deleteRestaurant(Long id);
}