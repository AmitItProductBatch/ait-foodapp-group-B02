package com.ait.app.service;

import java.util.Map;

public interface DeliveryFeeService {

    Map<String, Object> calculateDeliveryFee(
            Long restaurantAddressId,
            int userAddressId,
            int cartId
    );
}