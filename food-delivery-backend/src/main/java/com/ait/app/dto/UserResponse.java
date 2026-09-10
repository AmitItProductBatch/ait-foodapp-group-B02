package com.ait.app.dto;

import lombok.Data;

@Data
public class UserResponse {

	private String name;
	private long mobile;
	private String email;
	private String role;
}
