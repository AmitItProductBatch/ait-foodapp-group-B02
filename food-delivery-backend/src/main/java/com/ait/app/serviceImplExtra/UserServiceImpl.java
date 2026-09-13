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

		if (userRepository.existsByMobile(user.getMobile())) {

			throw new UserException("Mobile number already exists", HttpStatus.CONFLICT);
		}
		if (userRepository.existsByEmail(user.getEmail())) {

			throw new UserException("Email already exists", HttpStatus.CONFLICT);
		}

		userRepository.save(user);

	}

	@Override
	public UserResponse getUser(int id) {

		if (userRepository.existsById(id)) {

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

		} else {

			throw new UserException("User not found", HttpStatus.NOT_FOUND);
		}
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

			list.add(dto);

		}

		return list;
	}

	@Override
	public User updateUser(int id, User user) {

		if (userRepository.existsById(id)) {

			User ur = userRepository.findById(id).get();

			ur.setEmail(user.getEmail());
			ur.setMobile(user.getMobile());
			ur.setName(user.getName());
			ur.setPassword(user.getPassword());

			return userRepository.save(ur);

		} else {

			throw new UserException("User not found", HttpStatus.NOT_FOUND);
		}

	}

	@Override
	public void deleteAllUsers() {

		List<User> l = userRepository.findAll();

		if (l.isEmpty()) {

			throw new UserException("List is empty", HttpStatus.NOT_FOUND);

		} else {

			userRepository.deleteAll();
		}

	}
}