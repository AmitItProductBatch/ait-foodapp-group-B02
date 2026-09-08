package com.ait.app.serviceImplExtra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.RestaurantAddressRequestDto;
import com.ait.app.entity.Restaurant;
import com.ait.app.entity.RestaurantAddress;
import com.ait.app.repository.RestaurantAddressRepository;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.service.RestaurantAddressService;

@Service
public class RestaurantAddressServiceImpl implements RestaurantAddressService {
	
	@Autowired 
	RestaurantAddressRepository restaurantAddressRepository;;
	
	@Autowired
	RestaurantRepository restaurantRepository;

	@Override
	public RestaurantAddress saveRestaurantAddress(RestaurantAddressRequestDto restaurantAddressRequestDto) {
		// TODO Auto-generated method stub
		
		RestaurantAddress restaurantAddress = new RestaurantAddress();
		
		Restaurant restaurant = restaurantRepository.findById(restaurantAddressRequestDto.getRestaurantId()).get();
		
		restaurantAddress.setRestaurant(restaurant);
		
		restaurantAddress.setShopNo(restaurantAddressRequestDto.getShopNo());
		restaurantAddress.setStreet(restaurantAddressRequestDto.getStreet());
		restaurantAddress.setArea(restaurantAddressRequestDto.getArea());
		restaurantAddress.setCity(restaurantAddressRequestDto.getCity());
		restaurantAddress.setState(restaurantAddressRequestDto.getState());
		restaurantAddress.setPincode(restaurantAddressRequestDto.getPincode());
		restaurantAddress.setLatitude(restaurantAddressRequestDto.getLatitude());
		restaurantAddress.setLongitude(restaurantAddressRequestDto.getLongitude());
		
		 
		
		return restaurantAddressRepository.save(restaurantAddress);
	}

	@Override
	public RestaurantAddress getRestaurantAddressById(Long id) {
		
		return restaurantAddressRepository.findById(id).get();
	}

	@Override
	public void deleteRestaurantAddress(Long id) {
		
		restaurantAddressRepository.deleteById(id);
		
	}

}
