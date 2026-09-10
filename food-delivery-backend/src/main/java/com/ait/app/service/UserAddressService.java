package com.ait.app.service;

import com.ait.app.dto.UserAddressDto;
import com.ait.app.dto.UserAddressDto1;


public interface UserAddressService {

    void saveAddress(UserAddressDto addressDto);

    UserAddressDto1 getAddressById(int addressId);

    void deleteAddress(int addressId);
}