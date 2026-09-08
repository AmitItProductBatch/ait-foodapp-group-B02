package com.ait.app.serviceImplExtra;

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

			User user = userRepository.findById(id).get();

			UserResponse dto = new UserResponse();

			dto.setName(user.getName());
			dto.setEmail(user.getEmail());
			dto.setMobile(user.getMobile());
			dto.setRole(user.getRole());

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
}