package com.ait.app.serviceImplExtra;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.ait.app.dto.RestaurantRequestBody;
import com.ait.app.entity.Restaurant;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.service.RestaurantService;

@Service
public class RestaurantServiceImpl implements RestaurantService {
	
	@Autowired
	RestaurantRepository restaurantRepository;

	@Override
	public Restaurant saveRestaurant(RestaurantRequestBody restaurantRequestBody) {
		
		Restaurant restaurant = new Restaurant();
		restaurant.setName(restaurantRequestBody.getName());
		restaurant.setEmail(restaurantRequestBody.getEmail());
		restaurant.setPhone(restaurantRequestBody.getPhone());
		restaurant.setDescription(restaurantRequestBody.getDescription());
		
		return restaurantRepository.save(restaurant);
	}

	@Override
	public Restaurant getRestaurantById(Long id) {
		// TODO Auto-generated method stub
		return restaurantRepository.findById(id).orElse(null);
	}

	@Override
	public void deleteRestaurant(Long id) {
		// TODO Auto-generated method stub
		
		restaurantRepository.deleteById(id);
		
	}

	@Override
	public List<Restaurant> getRestaurantByCity(String city) {
		city.get
		return restaurantRepository.findByRestaurantAddressCity(city);
	}

	  

}
