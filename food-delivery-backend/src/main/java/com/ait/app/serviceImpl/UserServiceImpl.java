package com.ait.app.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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

}
