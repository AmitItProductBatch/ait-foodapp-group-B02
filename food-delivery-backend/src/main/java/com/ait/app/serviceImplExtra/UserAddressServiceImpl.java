package com.ait.app.serviceImplExtra;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.UserAddressDto;
import com.ait.app.dto.UserAddressDto1;
import com.ait.app.entity.User;
import com.ait.app.entity.UserAddress;
import com.ait.app.exception.UserAddressException;
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
    Optional<User> user = userRepo.findById(addressDto.getUserId());
   		if(user.isEmpty())
              throw new UserAddressException("User Not found with Id",HttpStatus.NOT_FOUND);   		
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
		 userAddress.setUser(user.get());

       userAddressRepo.save(userAddress);
        
	}


	@Override
	public UserAddressDto1 getAddressById(int addressId) {

		Optional<UserAddress> optionalAddress = userAddressRepo.findById(addressId);

		if (optionalAddress.isEmpty()) 
			throw new UserAddressException("UserAddress not found with id: " + addressId, HttpStatus.NOT_FOUND);
		
			UserAddress u = optionalAddress.get();
			return new UserAddressDto1(u.getHouseNo(), u.getBuildingName(), u.getStreet(), u.getLandmark(), u.getArea(),
					u.getCity(), u.getState(), u.getPincode(), u.getAddressType());
		
	}

	@Override
    public void deleteAddress(int addressId) {
    	  Optional<UserAddress> address = userAddressRepo.findById(addressId);
       
          if (address.isEmpty()) 
              throw new UserAddressException("Cannot delete. UserAddress not found with id: " + addressId, HttpStatus.NOT_FOUND);
              
            	  userAddressRepo.deleteById(addressId);
    }

	@Override
    public List<UserAddressDto1> fetchAllUserAddressesByUserId(int userId) {
        
		Optional<User> user = userRepo.findById(userId);
   		if(user.isEmpty())
              throw new UserAddressException("User Not found with Id",HttpStatus.NOT_FOUND);
   		
            	 
        List<UserAddress> addressList = user.get().getAddresses();
        
        if (addressList.isEmpty()) {
            throw new UserAddressException("No addresses found for user id: " + userId, HttpStatus.NOT_FOUND);
        }

        List<UserAddressDto1> dtoList = new java.util.ArrayList<>();
        for (UserAddress u : addressList) {
            dtoList.add(new UserAddressDto1(
                u.getHouseNo(),
                u.getBuildingName(),
                u.getStreet(),
                u.getLandmark(),
                u.getArea(),
                u.getCity(),
                u.getState(),
                u.getPincode(),
                u.getAddressType()
            ));
        }

        return dtoList;
    }

	@Override
	public UserAddressDto1 getAddressByType(String type, int userId) {
		UserAddress userA = null ;
		Optional<User> user = userRepo.findById(userId);
   		if(user.isEmpty())
              throw new UserAddressException("User Not found with Id",HttpStatus.NOT_FOUND); 
   		List<UserAddress> addressList = user.get().getAddresses();
   		for(	UserAddress us :  addressList) {
   			if(us.getAddressType().equalsIgnoreCase(type))
   				 userA = us; 
   		}
   		
		return new UserAddressDto1(userA.getHouseNo(), userA.getBuildingName(),userA.getStreet(),userA.getLandmark() , userA.getArea(), userA.getCity(), userA.getState(),userA.getPincode(), userA.getAddressType());
	}
}