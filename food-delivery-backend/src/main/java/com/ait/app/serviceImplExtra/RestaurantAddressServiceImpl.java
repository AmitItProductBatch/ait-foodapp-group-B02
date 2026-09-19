package com.ait.app.serviceImplExtra;

import java.util.List;

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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RestaurantAddressServiceImpl implements RestaurantAddressService {

    @Autowired
    RestaurantAddressRepository restaurantAddressRepository;

    @Autowired
    RestaurantRepository restaurantRepository;


    @Override
    public RestaurantAddress saveRestaurantAddress(
            RestaurantAddressRequestDto restaurantAddressRequestDto) {

        log.info("Creating restaurant address for restaurantId: {}",
                restaurantAddressRequestDto.getRestaurantId());

        RestaurantAddress restaurantAddress = new RestaurantAddress();

        Restaurant restaurant =
                restaurantRepository.findRestaurantById(
                        restaurantAddressRequestDto.getRestaurantId());

        if (restaurant == null) {

            log.warn("Restaurant not found for restaurantId: {}",
                    restaurantAddressRequestDto.getRestaurantId());

            throw new RestaurantAddressException(
                    "Restaurant not found",
                    HttpStatus.NOT_FOUND);
        }

        restaurantAddress.setRestaurant(restaurant);

        restaurantAddress.setShopNo(
                restaurantAddressRequestDto.getShopNo());

        restaurantAddress.setStreet(
                restaurantAddressRequestDto.getStreet());

        restaurantAddress.setArea(
                restaurantAddressRequestDto.getArea());

        restaurantAddress.setCity(
                restaurantAddressRequestDto.getCity());

        restaurantAddress.setState(
                restaurantAddressRequestDto.getState());

        restaurantAddress.setPincode(
                restaurantAddressRequestDto.getPincode());

        restaurantAddress.setLatitude(
                restaurantAddressRequestDto.getLatitude());

        restaurantAddress.setLongitude(
                restaurantAddressRequestDto.getLongitude());

        RestaurantAddress savedRestaurantAddress =
                restaurantAddressRepository.save(restaurantAddress);

        log.info("Restaurant address created successfully with id: {}",
                savedRestaurantAddress.getId());

        return savedRestaurantAddress;
    }


    @Override
    public List<RestaurantAddress> getAllRestaurantAddresses() {

        log.debug("Fetching all restaurant addresses");

        long startTime = System.currentTimeMillis();

        List<RestaurantAddress> restaurantAddresses =
                restaurantAddressRepository.findAll();

        long executionTime =
                System.currentTimeMillis() - startTime;

        log.info("Restaurant address search completed in {} ms",
                executionTime);

        if (restaurantAddresses.isEmpty()) {

            log.warn("No restaurant addresses found");

            throw new RestaurantAddressException(
                    "No restaurant addresses found",
                    HttpStatus.NOT_FOUND);
        }

        log.info("{} restaurant addresses found",
                restaurantAddresses.size());

        return restaurantAddresses;
    }


    @Override
    public RestaurantAddress getRestaurantAddressById(Long id) {

        log.debug("Fetching restaurant address with id: {}", id);

        RestaurantAddress restaurantAddress =
                restaurantAddressRepository.findRestaurantAddressById(id);

        if (restaurantAddress == null) {

            log.warn("Restaurant address not found with id: {}", id);

            throw new RestaurantAddressException(
                    "Restaurant Address not found",
                    HttpStatus.NOT_FOUND);
        }

        log.info("Restaurant address found with id: {}", id);

        return restaurantAddress;
    }


    @Override
    public RestaurantAddress updateRestaurantAddress(
            Long id,
            RestaurantAddressRequestDto restaurantAddressRequestDto) {

        log.info("Updating restaurant address with id: {}", id);

        RestaurantAddress restaurantAddress =
                restaurantAddressRepository.findRestaurantAddressById(id);

        if (restaurantAddress == null) {

            log.warn("Cannot update. Restaurant address not found with id: {}",
                    id);

            throw new RestaurantAddressException(
                    "Restaurant Address not found",
                    HttpStatus.NOT_FOUND);
        }

        Restaurant restaurant =
                restaurantRepository.findRestaurantById(
                        restaurantAddressRequestDto.getRestaurantId());

        if (restaurant == null) {

            log.warn("Cannot update address. Restaurant not found with restaurantId: {}",
                    restaurantAddressRequestDto.getRestaurantId());

            throw new RestaurantAddressException(
                    "Restaurant not found",
                    HttpStatus.NOT_FOUND);
        }

        restaurantAddress.setRestaurant(restaurant);

        restaurantAddress.setShopNo(
                restaurantAddressRequestDto.getShopNo());

        restaurantAddress.setStreet(
                restaurantAddressRequestDto.getStreet());

        restaurantAddress.setArea(
                restaurantAddressRequestDto.getArea());

        restaurantAddress.setCity(
                restaurantAddressRequestDto.getCity());

        restaurantAddress.setState(
                restaurantAddressRequestDto.getState());

        restaurantAddress.setPincode(
                restaurantAddressRequestDto.getPincode());

        restaurantAddress.setLatitude(
                restaurantAddressRequestDto.getLatitude());

        restaurantAddress.setLongitude(
                restaurantAddressRequestDto.getLongitude());

        RestaurantAddress updatedRestaurantAddress =
                restaurantAddressRepository.save(restaurantAddress);

        log.info("Restaurant address updated successfully with id: {}",
                updatedRestaurantAddress.getId());

        return updatedRestaurantAddress;
    }


    @Override
    public void deleteRestaurantAddress(Long id) {

        log.info("Deleting restaurant address with id: {}", id);

        RestaurantAddress restaurantAddress =
                restaurantAddressRepository.findRestaurantAddressById(id);

        if (restaurantAddress == null) {

            log.warn("Cannot delete. Restaurant address not found with id: {}",
                    id);

            throw new RestaurantAddressException(
                    "Restaurant Address not found",
                    HttpStatus.NOT_FOUND);
        }

        restaurantAddressRepository.delete(restaurantAddress);

        log.info("Restaurant address deleted successfully with id: {}", id);
    }
}