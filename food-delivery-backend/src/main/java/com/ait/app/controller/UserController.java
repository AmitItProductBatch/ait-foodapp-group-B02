package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.UserResponse;
import com.ait.app.entity.User;
import com.ait.app.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping("/register")
	public ResponseEntity addUser(@RequestBody User user) {

		userService.saveUser(user);
		return new ResponseEntity("User successfully added", HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUser(@PathVariable int id) {

		UserResponse user = userService.getUser(id);

		return new ResponseEntity(user, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable int id) {

		userService.deleteUser(id);

		return new ResponseEntity("User deleted successfully",HttpStatus.OK);
	}
}
