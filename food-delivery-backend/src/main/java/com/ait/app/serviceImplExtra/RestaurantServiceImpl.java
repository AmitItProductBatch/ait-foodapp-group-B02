package com.ait.app.serviceImplExtra;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import com.ait.app.dto.RestaurantRequestBody;
import com.ait.app.entity.Restaurant;
import com.ait.app.exception.RestaurantException;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.service.RestaurantService;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    RestaurantRepository restaurantRepository;

    
    @Override
    public Restaurant saveRestaurant(
            RestaurantRequestBody restaurantRequestBody) {

        Restaurant restaurant = new Restaurant();

        restaurant.setName(restaurantRequestBody.getName());
        restaurant.setEmail(restaurantRequestBody.getEmail());
        restaurant.setPhone(restaurantRequestBody.getPhone());
        restaurant.setDescription(restaurantRequestBody.getDescription());
        restaurant.setOpen(restaurantRequestBody.isOpen());

        return restaurantRepository.save(restaurant);
    }

    
    @Override
    public List<Restaurant> getAllRestaurants() {

        return restaurantRepository.findAll();
    }

    
    @Override
    public Restaurant getRestaurantById(Long id) {

        Restaurant restaurant =
                restaurantRepository.findById(id).orElse(null);

        if (restaurant == null) {
            throw new RestaurantException(
                    "Restaurant not found",
                    HttpStatus.NOT_FOUND);
        }

        return restaurant;
    }

    
    @Override
    public void deleteRestaurant(Long id) {

        if (!restaurantRepository.existsById(id)) {
            throw new RestaurantException(
                    "Restaurant not found",
                    HttpStatus.NOT_FOUND);
        }

        restaurantRepository.deleteById(id);
    }

    
    @Override
    public List<Restaurant> getRestaurantByCity(String city) {

        List<Restaurant> restaurants =
                restaurantRepository.findByRestaurantAddressCity(city);

        if (restaurants.isEmpty()) {
            throw new RestaurantException(
                    "No restaurants found in " + city,
                    HttpStatus.NOT_FOUND);
        }

        return restaurants;
    }
    
    @Override
    public Restaurant updateRestaurant(
            Long id,
            RestaurantRequestBody restaurantRequestBody) {

        Restaurant restaurant =
                restaurantRepository.findById(id).orElse(null);

        if (restaurant == null) {
            throw new RestaurantException(
                    "Restaurant not found",
                    HttpStatus.NOT_FOUND);
        }

        restaurant.setName(restaurantRequestBody.getName());
        restaurant.setEmail(restaurantRequestBody.getEmail());
        restaurant.setPhone(restaurantRequestBody.getPhone());
        restaurant.setDescription(restaurantRequestBody.getDescription());
        restaurant.setOpen(restaurantRequestBody.isOpen());

        return restaurantRepository.save(restaurant);
    }
}