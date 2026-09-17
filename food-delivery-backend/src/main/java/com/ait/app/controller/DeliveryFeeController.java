package com.ait.app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ait.app.service.DeliveryFeeService;

@RestController
@RequestMapping("/api/prices")
public class DeliveryFeeController {

    @Autowired
    DeliveryFeeService deliveryFeeService;


    @PostMapping("/delivery-fee")
    public ResponseEntity<Map<String, Object>>
            calculateDeliveryFee(
                    @RequestBody Map<String, Object> request) {

        Long restaurantAddressId =
                Long.valueOf(
                        request.get("restaurantAddressId")
                                .toString()
                );

        int userAddressId =
                Integer.parseInt(
                        request.get("userAddressId")
                                .toString()
                );

        int cartId =
                Integer.parseInt(
                        request.get("cartId")
                                .toString()
                );


        Map<String, Object> response =
                deliveryFeeService.calculateDeliveryFee(
                        restaurantAddressId,
                        userAddressId,
                        cartId
                );


        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }
}