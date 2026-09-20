
package com.ait.app.serviceImplExtra;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.RestaurantRequestBody;
import com.ait.app.entity.Restaurant;
import com.ait.app.exception.RestaurantException;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.service.RestaurantService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    RestaurantRepository restaurantRepository;


    @Override
    public Restaurant saveRestaurant(
            RestaurantRequestBody restaurantRequestBody) {

        log.info("Creating new restaurant: {}",
                restaurantRequestBody.getName());

        Restaurant restaurant = new Restaurant();

        restaurant.setName(restaurantRequestBody.getName());
        restaurant.setEmail(restaurantRequestBody.getEmail());
        restaurant.setPhone(restaurantRequestBody.getPhone());
        restaurant.setDescription(restaurantRequestBody.getDescription());
        restaurant.setOpen(restaurantRequestBody.isOpen());

        Restaurant savedRestaurant =
                restaurantRepository.save(restaurant);

        log.info("Restaurant created successfully with id: {}",
                savedRestaurant.getId());

        return savedRestaurant;
    }


    @Override
    public List<Restaurant> getAllRestaurants() {

        log.debug("Fetching all restaurants");

        List<Restaurant> restaurants =
                restaurantRepository.findAll();

        log.info("Fetched {} restaurants",
                restaurants.size());

        if (restaurants.isEmpty()) {
            log.warn("No restaurants found");
        }

        return restaurants;
    }


    @Override
    public Restaurant getRestaurantById(Long id) {

        log.debug("Fetching restaurant with id: {}", id);

        Restaurant restaurant =
                restaurantRepository.findById(id).orElse(null);

        if (restaurant == null) {

            log.warn("Restaurant not found with id: {}", id);

            throw new RestaurantException(
                    "Restaurant not found",
                    HttpStatus.NOT_FOUND);
        }

        log.info("Restaurant found with id: {}", id);

        return restaurant;
    }


    @Override
    public void deleteRestaurant(Long id) {

        log.info("Deleting restaurant with id: {}", id);

        if (!restaurantRepository.existsById(id)) {

            log.warn("Cannot delete restaurant. Restaurant not found with id: {}",
                    id);

            throw new RestaurantException(
                    "Restaurant not found",
                    HttpStatus.NOT_FOUND);
        }

        restaurantRepository.deleteById(id);

        log.info("Restaurant deleted successfully with id: {}",
                id);
    }


    @Override
    public List<Restaurant> getRestaurantByCity(String city) {

        log.debug("Searching restaurants in city: {}", city);

        long startTime = System.currentTimeMillis();

        List<Restaurant> restaurants =
                restaurantRepository.findByRestaurantAddressCity(city);

        long executionTime =
                System.currentTimeMillis() - startTime;

        log.info("Restaurant search completed for city: {} in {} ms",
                city, executionTime);

        if (restaurants.isEmpty()) {

            log.warn("No restaurants found in city: {}", city);

            throw new RestaurantException(
                    "No restaurants found in " + city,
                    HttpStatus.NOT_FOUND);
        }

        log.info("Found {} restaurants in city: {}",
                restaurants.size(), city);

        return restaurants;
    }


    @Override
    public Restaurant updateRestaurant(
            Long id,
            RestaurantRequestBody restaurantRequestBody) {

        log.info("Updating restaurant with id: {}", id);

        Restaurant restaurant =
                restaurantRepository.findById(id).orElse(null);

        if (restaurant == null) {

            log.warn("Cannot update restaurant. Restaurant not found with id: {}",
                    id);

            throw new RestaurantException(
                    "Restaurant not found",
                    HttpStatus.NOT_FOUND);
        }

        restaurant.setName(restaurantRequestBody.getName());
        restaurant.setEmail(restaurantRequestBody.getEmail());
        restaurant.setPhone(restaurantRequestBody.getPhone());
        restaurant.setDescription(restaurantRequestBody.getDescription());
        restaurant.setOpen(restaurantRequestBody.isOpen());

        Restaurant updatedRestaurant =
                restaurantRepository.save(restaurant);

        log.info("Restaurant updated successfully with id: {}",
                id);

        return updatedRestaurant;
    }
}

