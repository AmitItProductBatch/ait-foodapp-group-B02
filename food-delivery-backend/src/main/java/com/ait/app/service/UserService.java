package com.ait.app.service;

import java.util.List;

import com.ait.app.dto.UserResponse;
import com.ait.app.entity.User;

public interface UserService {

	public void saveUser(User user);

	public UserResponse getUser(int id);

	public void deleteUser(int id);

	public List<UserResponse> getAllUsers();

	public User updateUser(int id, User user);

	public void deleteAllUsers();
}
