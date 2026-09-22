package com.ait.app.dto;

import lombok.Data;

@Data
public class UserDto {

	private int roleId;
	private String name;
	private String mobile;
	private String email;
	private String password;
}
