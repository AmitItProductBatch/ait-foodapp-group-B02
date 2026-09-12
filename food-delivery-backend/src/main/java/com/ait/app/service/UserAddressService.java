package com.ait.app.service;



import java.util.List;

import com.ait.app.dto.UserAddressDto;
import com.ait.app.dto.UserAddressDto1;


public interface UserAddressService {

    void saveAddress(UserAddressDto addressDto);

    UserAddressDto1 getAddressById(int addressId);

    void deleteAddress(int addressId);
    
    List<UserAddressDto1> fetchAllUserAddressesByUserId(int userId); 
    
    UserAddressDto1 getAddressByType(String type,int userId);
}