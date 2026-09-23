package com.ait.app.serviceImplExtra;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.UserDto;
import com.ait.app.dto.UserResponse;
import com.ait.app.entity.Role;
import com.ait.app.entity.User;
import com.ait.app.exception.UserException;
import com.ait.app.repository.RoleRepository;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UserAddressServiceImpl uAddServ;

	@Override
	public void saveUser(UserDto userDto) {

		log.info("Starting user registration");

		if (userDto == null) {

			log.warn("User details are empty");
			throw new UserException("User details cannot be empty", HttpStatus.BAD_REQUEST);
		}

		if (userDto.getName() == null || userDto.getName().isEmpty()) {

			log.warn("User name is missing");
			throw new UserException("Please enter your name", HttpStatus.BAD_REQUEST);
		}

		if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {

			log.warn("User email is missing");
			throw new UserException("Please enter your email", HttpStatus.BAD_REQUEST);
		}

		if (!userDto.getEmail().contains("@")) {

			log.warn("Invalid email format");
			throw new UserException("Please enter a valid email address", HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByEmail(userDto.getEmail())) {

			log.warn("Email already exists");
			throw new UserException("Email is already registered", HttpStatus.CONFLICT);
		}

		if (userDto.getMobile() == null || userDto.getMobile().isEmpty()) {

			log.warn("Mobile number is missing");
			throw new UserException("Please enter your mobile number", HttpStatus.BAD_REQUEST);
		}

		if (!userDto.getMobile().matches("\\d{10}")) {

			log.warn("Invalid mobile number");
			throw new UserException("Mobile number must be 10 digits", HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByMobile(userDto.getMobile())) {

			log.warn("Mobile number already exists");
			throw new UserException("Mobile number is already registered", HttpStatus.CONFLICT);
		}

		if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {

			log.warn("Password is missing");
			throw new UserException("Please enter your password", HttpStatus.BAD_REQUEST);
		}

		Optional<Role> o = roleRepository.findById(userDto.getRoleId());
		if (o.isEmpty()) {
			
			throw new UserException("Role not found", HttpStatus.NOT_FOUND);
		}

		User user = new User();
		
		user.setName(userDto.getName());
		user.setMobile(userDto.getMobile());
		user.setEmail(userDto.getEmail());
		user.setPassword(userDto.getPassword());
		user.setRole(o.get());
		userRepository.save(user);

		log.info("User registered successfully");
	}

	@Override
	public UserResponse getUser(int id) {

		log.info("Getting user with id: {}", id);

		Optional<User> o = userRepository.findById(id);

		if (o.isEmpty()) {

			log.warn("User not found with id: {}", id);
			throw new UserException("User not found", HttpStatus.NOT_FOUND);
		}

		User user = o.get();
		UserResponse dto = new UserResponse();

		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setEmail(user.getEmail());
		dto.setMobile(user.getMobile());
		dto.setRole(user.getRole().getRName());
		dto.setAddresses(uAddServ.fetchAllUserAddressesByUserId(id));

		log.info("User get successfully with id: {}", id);
		return dto;

	}

	@Override
	public void deleteUser(int id) {

		log.info("Deleting user with id: {}", id);

		if (userRepository.existsById(id)) {

			userRepository.deleteById(id);

			log.info("User deleted successfully with id: {}", id);

		} else {

			log.warn("User not found with id: {}", id);
			throw new UserException("User not found", HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public List<UserResponse> getAllUsers() {

		log.info("Getting all users");

		List<User> l = userRepository.findAll();

		List<UserResponse> list = new ArrayList<>();

		for (User user : l) {

			UserResponse dto = new UserResponse();
			dto.setId(user.getId());
			dto.setName(user.getName());
			dto.setMobile(user.getMobile());
			dto.setEmail(user.getEmail());
			dto.setRole(user.getRole().getRName());
			dto.setAddresses(uAddServ.fetchAllUserAddressesByUserId(user.getId()));
			list.add(dto);

		}

		log.info("Successfully fetched {} users", list.size());
		return list;

	}

	@Override
	public UserResponse updateUser(int id, User user) {

		log.info("Updating user with id: {}", id);

		Optional<User> o = userRepository.findById(id);

		if (o.isEmpty()) {

			log.warn("User details are empty");
			throw new UserException("User not found", HttpStatus.NOT_FOUND);
		}

		User ur = o.get();

		if (user.getName() != null && !user.getName().isEmpty()) {

			log.debug("Updating name for user id: {}", id);
			ur.setName(user.getName());
		}

		if (user.getEmail() != null && !user.getEmail().isEmpty()) {

			log.debug("Updating email for user id: {}", id);

			if (!user.getEmail().endsWith("@gmail.com")) {

				log.warn("Invalid email format for user id: {}", id);
				throw new UserException("Please use a @gmail.com", HttpStatus.BAD_REQUEST);
			}

			if (userRepository.existsEmailForOtherUser(user.getEmail(), id)) {

				log.warn("Email already exists for another user");
				throw new UserException("Email already exists", HttpStatus.CONFLICT);
			}

			ur.setEmail(user.getEmail());
		}

		if (user.getMobile() != null && !user.getMobile().isEmpty()) {

			log.debug("Updating mobile number for user id: {}", id);

			if (!user.getMobile().matches("\\d{10}")) {

				log.warn("Invalid mobile number for user id: {}", id);
				throw new UserException("Mobile number must be 10 digits", HttpStatus.BAD_REQUEST);
			}

			if (userRepository.existsMobileForOtherUser(user.getMobile(), id)) {

				log.warn("Mobile number already exists for another user");
				throw new UserException("Mobile number already exists", HttpStatus.CONFLICT);
			}

			ur.setMobile(user.getMobile());
		}

		User updatedUser = userRepository.save(ur);

		UserResponse response = new UserResponse();

		response.setId(updatedUser.getId());
		response.setName(updatedUser.getName());
		response.setEmail(updatedUser.getEmail());
		response.setMobile(updatedUser.getMobile());
		response.setAddresses(uAddServ.fetchAllUserAddressesByUserId(id));

		log.info("User updated successfully with id: {}", id);

		return response;
	}

	@Override
	public void deleteAllUsers() {

		log.info("Deleting all users");

		List<User> l = userRepository.findAll();

		if (l.isEmpty()) {

			log.warn("Users list is empty");
			throw new UserException("Users list is empty", HttpStatus.NOT_FOUND);
		}

		userRepository.deleteAll();

		log.info("All users deleted successfully");

	}
}