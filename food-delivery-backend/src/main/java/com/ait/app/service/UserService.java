package com.ait.app.service;

import com.ait.app.dto.UserResponse;
import com.ait.app.entity.User;

public interface UserService {

	public void saveUser(User user);

	UserResponse getUser(int id);

	void deleteUser(int id);
}
