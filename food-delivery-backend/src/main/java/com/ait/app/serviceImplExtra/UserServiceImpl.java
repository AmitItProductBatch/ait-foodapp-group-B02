package com.ait.app.serviceImplExtra;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.UserResponse;
import com.ait.app.entity.User;
import com.ait.app.exception.UserException;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserAddressServiceImpl uAddServ;

	@Override
	public void saveUser(User user) {

		if (user == null) {

			throw new UserException("User details cannot be empty", HttpStatus.BAD_REQUEST);
		}

		if (user.getName() == null || user.getName().isEmpty()) {

			throw new UserException("Please enter your name", HttpStatus.BAD_REQUEST);
		}

		if (user.getRole() == null || user.getRole().isEmpty()) {

			throw new UserException("Please enter your role", HttpStatus.BAD_REQUEST);
		}

		if (user.getEmail() == null || user.getEmail().isEmpty()) {

			throw new UserException("Please enter your email", HttpStatus.BAD_REQUEST);
		}

		if (!user.getEmail().endsWith("@gmail.com")) {

			throw new UserException("Please use a @gmail.com", HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByEmail(user.getEmail())) {

			throw new UserException("Email is already registered", HttpStatus.CONFLICT);
		}

		if (user.getMobile() == null || user.getMobile().isEmpty()) {

			throw new UserException("Please enter your mobile number", HttpStatus.BAD_REQUEST);
		}

		if (!user.getMobile().matches("\\d{10}")) {

			throw new UserException("Mobile number must be 10 digits", HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByMobile(user.getMobile())) {

			throw new UserException("Mobile number is already registered", HttpStatus.CONFLICT);
		}

		if (user.getPassword() == null || user.getPassword().isEmpty()) {

			throw new UserException("Please enter your password", HttpStatus.BAD_REQUEST);
		}

		userRepository.save(user);

	}

	@Override
	public UserResponse getUser(int id) {

		Optional<User> o = userRepository.findById(id);

		if (o.isEmpty()) {

			throw new UserException("User not found", HttpStatus.NOT_FOUND);
		}

		User user = o.get();
		UserResponse dto = new UserResponse();

		dto.setName(user.getName());
		dto.setEmail(user.getEmail());
		dto.setMobile(user.getMobile());
		dto.setRole(user.getRole());
		dto.setAddresses(uAddServ.fetchAllUserAddressesByUserId(id));
		return dto;

	}

	@Override
	public void deleteUser(int id) {

		if (userRepository.existsById(id)) {

			userRepository.deleteById(id);

		} else {

			throw new UserException("User not found", HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public List<UserResponse> getAllUsers() {

		List<User> l = userRepository.findAll();

		if (l.isEmpty()) {

			throw new UserException("Users not found", HttpStatus.NOT_FOUND);
		}

		List<UserResponse> list = new ArrayList();

		for (User user : l) {

			UserResponse dto = new UserResponse();
			dto.setName(user.getName());
			dto.setMobile(user.getMobile());
			dto.setEmail(user.getEmail());
			dto.setRole(user.getRole());
			dto.setAddresses(uAddServ.fetchAllUserAddressesByUserId(user.getId()));
			list.add(dto);

		}

		return list;
	}

	@Override
	public UserResponse updateUser(int id, User user) {

		Optional<User> o = userRepository.findById(id);

		if (o.isEmpty()) {

			throw new UserException("User not found", HttpStatus.NOT_FOUND);
		}

		User ur = o.get();

		if (user.getName() != null && !user.getName().isEmpty()) {

			ur.setName(user.getName());
		}

		if (user.getEmail() != null && !user.getEmail().isEmpty()) {

			if (!user.getEmail().endsWith("@gmail.com")) {

				throw new UserException("Please use a @gmail.com", HttpStatus.BAD_REQUEST);
			}

			if (userRepository.existsEmailForOtherUser(user.getEmail(), id)) {

				throw new UserException("Email already exists", HttpStatus.CONFLICT);
			}

			ur.setEmail(user.getEmail());
		}

		if (user.getMobile() != null && !user.getMobile().isEmpty()) {

			if (!user.getMobile().matches("\\d{10}")) {

				throw new UserException("Mobile number must be 10 digits", HttpStatus.BAD_REQUEST);
			}

			if (userRepository.existsMobileForOtherUser(user.getMobile(), id)) {

				throw new UserException("Mobile number already exists", HttpStatus.CONFLICT);
			}

			ur.setMobile(user.getMobile());
		}

		User updatedUser = userRepository.save(ur);

		UserResponse response = new UserResponse();

		response.setName(updatedUser.getName());
		response.setEmail(updatedUser.getEmail());
		response.setMobile(updatedUser.getMobile());
		response.setRole(updatedUser.getRole());
		response.setAddresses(uAddServ.fetchAllUserAddressesByUserId(id));

		return response;
	}

	@Override
	public void deleteAllUsers() {

		List<User> l = userRepository.findAll();

		if (l.isEmpty()) {

			throw new UserException("Users list is empty", HttpStatus.NOT_FOUND);
		}

		userRepository.deleteAll();
	}
}