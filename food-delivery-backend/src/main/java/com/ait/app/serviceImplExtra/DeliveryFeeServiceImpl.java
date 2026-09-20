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


        double baseFee = 30;
        double baseDistance = 2;
        double extraFeePerKm = 10;
        double freeDeliveryThreshold = 500;



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


        Optional<Cart> cartOptional =
                cartRepository.findById(cartId);

        if (!cartOptional.isPresent()) {

            throw new DeliveryFeeException(
                    "Cart Not Found",
                    HttpStatus.NOT_FOUND
            );
        }

        Cart cart = cartOptional.get();



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



        double restaurantLatitude =
                restaurantAddress.getLatitude();

        double restaurantLongitude =
                restaurantAddress.getLongitude();

        double userLatitude =
                userAddress.getLatitude();

        double userLongitude =
                userAddress.getLongitude();


        double distance =
                distanceService.getDistance(
                        restaurantLongitude,
                        restaurantLatitude,
                        userLongitude,
                        userLatitude
                );


        double deliveryFee;


        // Free delivery
        if (cart.getTotalAmount()
                >= freeDeliveryThreshold) {

            deliveryFee = 0;

        }

    
        else if (distance <= baseDistance) {

            deliveryFee = baseFee;

        }

      
        else {

            double extraDistance =
                    distance - baseDistance;

            deliveryFee =
                    baseFee
                    + (extraDistance * extraFeePerKm);
        }


      
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