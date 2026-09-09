package com.ait.app.serviceImplExtra;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.UserAddressDto;
import com.ait.app.dto.UserAddressDto1;
import com.ait.app.entity.User;
import com.ait.app.entity.UserAddress;
import com.ait.app.repository.UserAddressRepository;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.UserAddressService;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressRepository userAddressRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public void saveAddress(UserAddressDto addressDto) {
//        User user = userRepo.findById(addressDto.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + addressDto.getUserId()));

        UserAddress userAddress = new UserAddress();
        userAddress.setHouseNo(addressDto.getHouseNo());
        userAddress.setBuildingName(addressDto.getBuildingName());
        userAddress.setStreet(addressDto.getStreet());
        userAddress.setLandmark(addressDto.getLandmark());
        userAddress.setArea(addressDto.getArea());
        userAddress.setCity(addressDto.getCity());
        userAddress.setState(addressDto.getState());
        userAddress.setPincode(addressDto.getPincode());
        userAddress.setAddressType(addressDto.getAddressType());
        //userAddress.setUser(user);

        userAddressRepo.save(userAddress);
    }

    @Override
    public UserAddressDto1 getAddressById(int addressId) {
        UserAddress u = userAddressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("UserAddress not found with id: " + addressId));

        return new UserAddressDto1(
            u.getHouseNo(),
            u.getBuildingName(),
            u.getStreet(),
            u.getLandmark(),
            u.getArea(),
            u.getCity(),
            u.getState(),
            u.getPincode(),
            u.getAddressType()
        );
    }

    @Override
    public void deleteAddress(int addressId) {
        if (!userAddressRepo.existsById(addressId)) {
            throw new RuntimeException("Cannot delete. UserAddress not found with id: " + addressId);
        }
        userAddressRepo.deleteById(addressId);
    }
}