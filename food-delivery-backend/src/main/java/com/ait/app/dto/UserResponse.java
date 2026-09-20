package com.ait.app.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserResponse {

	private int id;
	private String name;
	private String mobile;
	private String email;
	private String role;
	private List<UserAddressDto1>	addresses;
}
