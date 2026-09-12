package com.ait.app.serviceImplExtra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.RestaurantAddressRequestDto;
import com.ait.app.entity.Restaurant;
import com.ait.app.entity.RestaurantAddress;
import com.ait.app.exception.RestaurantAddressException;
import com.ait.app.repository.RestaurantAddressRepository;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.service.RestaurantAddressService;

@Service
public class RestaurantAddressServiceImpl implements RestaurantAddressService {
	
	@Autowired 
	RestaurantAddressRepository restaurantAddressRepository;
	
	@Autowired
	RestaurantRepository restaurantRepository;

	@Override
	public RestaurantAddress saveRestaurantAddress(RestaurantAddressRequestDto restaurantAddressRequestDto) {
		// TODO Auto-generated method stub
		
		RestaurantAddress restaurantAddress = new RestaurantAddress();
		
		Restaurant restaurant = restaurantRepository.findById(restaurantAddressRequestDto.getRestaurantId()).orElse(null);
		
		if(restaurant==null) {
			throw new RestaurantAddressException("Restaurant not found", HttpStatus.NOT_FOUND);
		}
		
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
		
		RestaurantAddress restaurantAddress = restaurantAddressRepository.findById(id).orElse(null);
		
		if(restaurantAddress==null) {
			throw new RestaurantAddressException("Restaurant Address not found", HttpStatus.NOT_FOUND);
		}
		
		return restaurantAddress;
	}

	@Override
	public void deleteRestaurantAddress(Long id) {
		
		if(!restaurantAddressRepository.existsById(id)) {
			throw new RestaurantAddressException("Restaurant Address not found", HttpStatus.NOT_FOUND);
		}
		
		restaurantAddressRepository.deleteById(id);
		
	}

}
