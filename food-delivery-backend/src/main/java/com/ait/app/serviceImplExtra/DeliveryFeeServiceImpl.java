package com.ait.app.serviceImplExtra;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.entity.Cart;
import com.ait.app.entity.RestaurantAddress;
import com.ait.app.entity.UserAddress;
import com.ait.app.exception.DeliveryFeeException;
import com.ait.app.repository.CartRepository;
import com.ait.app.repository.RestaurantAddressRepository;
import com.ait.app.repository.UserAddressRepository;
import com.ait.app.service.DeliveryFeeService;
import com.ait.app.service.DistanceService;

@Service
public class DeliveryFeeServiceImpl
        implements DeliveryFeeService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    UserAddressRepository userAddressRepository;

    @Autowired
    RestaurantAddressRepository restaurantAddressRepository;

    @Autowired
    DistanceService distanceService;


    @Override
    public Map<String, Object> calculateDeliveryFee(
            Long restaurantAddressId,
            int userAddressId,
            int cartId) {

        // -----------------------------------------
        // FEE RULES
        // -----------------------------------------

        double baseFee = 30;
        double baseDistance = 2;
        double extraFeePerKm = 10;
        double freeDeliveryThreshold = 500;


        // -----------------------------------------
        // FIND RESTAURANT ADDRESS
        // -----------------------------------------

        Optional<RestaurantAddress> restaurantAddressOptional =
                restaurantAddressRepository
                        .findById(restaurantAddressId);

        if (!restaurantAddressOptional.isPresent()) {

            throw new DeliveryFeeException(
                    "Restaurant Address Not Found",
                    HttpStatus.BAD_REQUEST
            );
        }

        RestaurantAddress restaurantAddress =
                restaurantAddressOptional.get();


        // -----------------------------------------
        // FIND USER ADDRESS
        // -----------------------------------------

        Optional<UserAddress> userAddressOptional =
                userAddressRepository
                        .findById(userAddressId);

        if (!userAddressOptional.isPresent()) {

            throw new DeliveryFeeException(
                    "User Address Not Found",
                    HttpStatus.BAD_REQUEST
            );
        }

        UserAddress userAddress =
                userAddressOptional.get();


        // -----------------------------------------
        // FIND CART
        // -----------------------------------------

        Optional<Cart> cartOptional =
                cartRepository.findById(cartId);

        if (!cartOptional.isPresent()) {

            throw new DeliveryFeeException(
                    "Cart Not Found",
                    HttpStatus.NOT_FOUND
            );
        }

        Cart cart = cartOptional.get();


        // -----------------------------------------
        // CHECK COORDINATES
        // -----------------------------------------

        if (restaurantAddress.getLatitude() == 0
                || restaurantAddress.getLongitude() == 0) {

            throw new DeliveryFeeException(
                    "Restaurant coordinates not available",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (userAddress.getLatitude() == 0
                || userAddress.getLongitude() == 0) {

            throw new DeliveryFeeException(
                    "User coordinates not available",
                    HttpStatus.BAD_REQUEST
            );
        }


        // -----------------------------------------
        // GET COORDINATES
        // -----------------------------------------

        double restaurantLatitude =
                restaurantAddress.getLatitude();

        double restaurantLongitude =
                restaurantAddress.getLongitude();

        double userLatitude =
                userAddress.getLatitude();

        double userLongitude =
                userAddress.getLongitude();


        // -----------------------------------------
        // CALCULATE ROAD DISTANCE
        // -----------------------------------------

        double distance =
                distanceService.getDistance(
                        restaurantLongitude,
                        restaurantLatitude,
                        userLongitude,
                        userLatitude
                );


        // -----------------------------------------
        // CALCULATE DELIVERY FEE
        // -----------------------------------------

        double deliveryFee;


        // Free delivery
        if (cart.getTotalAmount()
                >= freeDeliveryThreshold) {

            deliveryFee = 0;

        }

        // First 2 km
        else if (distance <= baseDistance) {

            deliveryFee = baseFee;

        }

        // More than 2 km
        else {

            double extraDistance =
                    distance - baseDistance;

            deliveryFee =
                    baseFee
                    + (extraDistance * extraFeePerKm);
        }


        // -----------------------------------------
        // RESPONSE
        // -----------------------------------------

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "distance",
                Math.round(distance * 100.0) / 100.0
        );

        response.put(
                "deliveryFee",
                Math.round(deliveryFee * 100.0) / 100.0
        );

        return response;
    }
}