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
import com.ait.app.service.GeocodingService;
import com.ait.app.service.RestaurantAddressService;
@Service
public class RestaurantAddressServiceImpl
        implements RestaurantAddressService {

    @Autowired
    RestaurantAddressRepository restaurantAddressRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    GeocodingService geocodingService;


    @Override
    public RestaurantAddress saveRestaurantAddress(
            RestaurantAddressRequestDto dto) {

        Restaurant restaurant =
                restaurantRepository.findById(dto.getRestaurantId())
                        .orElse(null);

        if (restaurant == null) {

            throw new RestaurantAddressException(
                    "Restaurant not found",
                    HttpStatus.NOT_FOUND
            );
        }


        RestaurantAddress restaurantAddress =
                new RestaurantAddress();

        restaurantAddress.setRestaurant(restaurant);

        restaurantAddress.setShopNo(
                dto.getShopNo()
        );

        restaurantAddress.setStreet(
                dto.getStreet()
        );

        restaurantAddress.setArea(
                dto.getArea()
        );

        restaurantAddress.setCity(
                dto.getCity()
        );

        restaurantAddress.setState(
                dto.getState()
        );

        restaurantAddress.setPincode(
                dto.getPincode()
        );


        // Build address for geocoding
        String fullAddress =
                dto.getStreet() + ", "
                + dto.getArea() + ", "
                + dto.getCity() + ", "
                + dto.getState() + ", India, "
                + dto.getPincode();


        // Convert address to latitude and longitude
        double[] coordinates =
                geocodingService.getCoordinates(fullAddress);


        // Save latitude and longitude
        restaurantAddress.setLatitude(coordinates[0]);
        restaurantAddress.setLongitude(coordinates[1]);


        // Save restaurant address
        return restaurantAddressRepository.save(
                restaurantAddress
        );
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
