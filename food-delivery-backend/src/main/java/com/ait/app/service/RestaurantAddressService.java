package com.ait.app.service;

import java.util.List;

import com.ait.app.dto.RestaurantAddressRequestDto;
import com.ait.app.entity.RestaurantAddress;

public interface RestaurantAddressService {

    RestaurantAddress saveRestaurantAddress(
            RestaurantAddressRequestDto restaurantAddressRequestDto);

    List<RestaurantAddress> getAllRestaurantAddresses();

    RestaurantAddress getRestaurantAddressById(Long id);

    RestaurantAddress updateRestaurantAddress(
            Long id,
            RestaurantAddressRequestDto restaurantAddressRequestDto);

    void deleteRestaurantAddress(Long id);
}