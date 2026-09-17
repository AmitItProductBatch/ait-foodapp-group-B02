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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserAddressServiceImpl implements UserAddressService {

	@Autowired
	private static final Logger log = LoggerFactory.getLogger(UserAddressServiceImpl.class);

	@Autowired
	private UserAddressRepository userAddressRepo;

	@Autowired
	private UserRepository userRepo;

	@Override
	public void saveAddress(UserAddressDto addressDto) {
		log.info("Executing saveAddress for userId: {}", addressDto.getUserId());
		Optional<User> user = userRepo.findById(addressDto.getUserId());
		if (user.isEmpty()) {
			log.error("User Not found with Id: {}", addressDto.getUserId());
			throw new UserAddressException("User Not found with Id", HttpStatus.NOT_FOUND);
		}
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
		userAddress.setLatitude(addressDto.getLatitude());
		userAddress.setLongitude(addressDto.getLongitude());
		userAddress.setUser(user.get());

		userAddressRepo.save(userAddress);
		log.info("Successfully saved address for userId: {}", addressDto.getUserId());
	}

	@Override
	public UserAddressDto1 getAddressById(int addressId) {
		log.info("Executing getAddressById for addressId: {}", addressId);
		Optional<UserAddress> optionalAddress = userAddressRepo.findById(addressId);

		if (optionalAddress.isEmpty()) {
			log.error("UserAddress not found with id: {}", addressId);
			throw new UserAddressException("UserAddress not found with id: " + addressId, HttpStatus.NOT_FOUND);
		}

		UserAddress u = optionalAddress.get();
		return new UserAddressDto1(u.getId(), u.getHouseNo(), u.getBuildingName(), u.getStreet(), u.getLandmark(),
				u.getArea(), u.getCity(), u.getState(), u.getPincode(), u.getAddressType(), u.getLatitude(),
				u.getLongitude());

	}

	@Override
	public void deleteAddress(int addressId) {
		log.info("Executing deleteAddress for addressId: {}", addressId);
		Optional<UserAddress> address = userAddressRepo.findById(addressId);

		if (address.isEmpty()) {
			log.error("UserAddress not found for deletion with id: {}", addressId);
			throw new UserAddressException("Cannot delete. UserAddress not found with id: " + addressId,
					HttpStatus.NOT_FOUND);
		}

		userAddressRepo.deleteById(addressId);
		log.info("Successfully deleted address with id: {}", addressId);
	}

	@Override
	public List<UserAddressDto1> fetchAllUserAddressesByUserId(int userId) {
		log.info("Executing fetchAllUserAddressesByUserId for userId: {}", userId);
		Optional<User> user = userRepo.findById(userId);
		if (user.isEmpty()) {
			log.error("User Not found with Id: {}", userId);
			throw new UserAddressException("User Not found with Id", HttpStatus.NOT_FOUND);
		}

		List<UserAddress> addressList = user.get().getAddresses();

		if (addressList.isEmpty()) {
			log.error("No addresses found for user id: {}", userId);
			throw new UserAddressException("No addresses found for user id: " + userId, HttpStatus.NOT_FOUND);
		}

		List<UserAddressDto1> dtoList = new java.util.ArrayList<>();
		for (UserAddress u : addressList) {
			dtoList.add(new UserAddressDto1(u.getId(), u.getHouseNo(), u.getBuildingName(), u.getStreet(),
					u.getLandmark(), u.getArea(), u.getCity(), u.getState(), u.getPincode(), u.getAddressType(),
					u.getLatitude(), u.getLongitude()));
		}

		return dtoList;
	}

	@Override
	public UserAddressDto1 getAddressByType(String type, int userId) {
		log.info("Executing getAddressByType for type: {} and userId: {}", type, userId);
		UserAddress userA = null;
		Optional<User> user = userRepo.findById(userId);
		if (user.isEmpty()) {
			log.error("User Not found with Id: {}", userId);
			throw new UserAddressException("User Not found with Id", HttpStatus.NOT_FOUND);
		}
		List<UserAddress> addressList = user.get().getAddresses();
		for (UserAddress us : addressList) {
			if (us.getAddressType().equalsIgnoreCase(type)) {
				userA = us;
				break;
			}
		}
		if (userA == null) {
			log.error("UserAddress not found with type: {}", type);
			throw new UserAddressException("UserAddress not found with type: " + type, HttpStatus.NOT_FOUND);
		}
		return new UserAddressDto1(userA.getId(), userA.getHouseNo(), userA.getBuildingName(), userA.getStreet(),
				userA.getLandmark(), userA.getArea(), userA.getCity(), userA.getState(), userA.getPincode(),
				userA.getAddressType(), userA.getLatitude(), userA.getLongitude());
	}

	@Override
	public UserAddressDto1 updateAddress(int addressId, UserAddressDto addressDto) {
		log.info("Executing updateAddress for addressId: {}", addressId);
		Optional<UserAddress> optionalAddress = userAddressRepo.findById(addressId);
		if (optionalAddress.isEmpty()) {
			log.error("UserAddress not found with id: {}", addressId);
			throw new UserAddressException("UserAddress not found with id: " + addressId, HttpStatus.NOT_FOUND);
		}

		UserAddress userAddress = optionalAddress.get();

		userAddress.setHouseNo(addressDto.getHouseNo());
		userAddress.setBuildingName(addressDto.getBuildingName());
		userAddress.setStreet(addressDto.getStreet());
		userAddress.setLandmark(addressDto.getLandmark());
		userAddress.setArea(addressDto.getArea());
		userAddress.setCity(addressDto.getCity());
		userAddress.setState(addressDto.getState());
		userAddress.setPincode(addressDto.getPincode());
		userAddress.setAddressType(addressDto.getAddressType());
		userAddress.setLatitude(addressDto.getLatitude());
		userAddress.setLongitude(addressDto.getLongitude());

		if (addressDto.getUserId() > 0 && userAddress.getUser().getId() != addressDto.getUserId()) {
			Optional<User> user = userRepo.findById(addressDto.getUserId());
			if (user.isEmpty()) {
				log.error("User Not found with Id: {}", addressDto.getUserId());
				throw new UserAddressException("User Not found with Id", HttpStatus.NOT_FOUND);
			}
			userAddress.setUser(user.get());
		}

		UserAddress updatedAddress = userAddressRepo.save(userAddress);
		log.info("Successfully updated address with id: {}", addressId);

		return new UserAddressDto1(updatedAddress.getId(), updatedAddress.getHouseNo(),
				updatedAddress.getBuildingName(), updatedAddress.getStreet(), updatedAddress.getLandmark(),
				updatedAddress.getArea(), updatedAddress.getCity(), updatedAddress.getState(),
				updatedAddress.getPincode(), updatedAddress.getAddressType(), updatedAddress.getLatitude(),
				updatedAddress.getLongitude());
	}
}