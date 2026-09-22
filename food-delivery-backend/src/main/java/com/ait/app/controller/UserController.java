package com.ait.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.UserDto;
import com.ait.app.dto.UserResponse;
import com.ait.app.entity.User;
import com.ait.app.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping
	public ResponseEntity addUser(@RequestBody UserDto dto) {

		userService.saveUser(dto);
		return new ResponseEntity("User successfully added", HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity getUser(@PathVariable int id) {

		UserResponse user = userService.getUser(id);

		return new ResponseEntity(user, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity deleteUser(@PathVariable int id) {

		userService.deleteUser(id);

		return new ResponseEntity("User deleted successfully", HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity getAllUsers() {

		List<UserResponse> list = userService.getAllUsers();

		return new ResponseEntity(list, HttpStatus.OK);
	}

	@PutMapping("/{id}")
	public ResponseEntity updateUser(@PathVariable int id, @RequestBody User user) {

		UserResponse updatedUser = userService.updateUser(id, user);

		return new ResponseEntity(updatedUser, HttpStatus.OK);
	}

	@DeleteMapping
	public ResponseEntity deleteAllUsers() {

		userService.deleteAllUsers();

		return new ResponseEntity("All users deleted successfully", HttpStatus.OK);
	}
}
