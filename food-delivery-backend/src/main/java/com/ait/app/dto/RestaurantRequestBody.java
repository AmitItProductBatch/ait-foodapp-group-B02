package com.ait.app.dto;

import org.springframework.stereotype.Service;

import jakarta.persistence.Column;

@Service
public class RestaurantRequestBody {
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false, unique = true)
	private String phone;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	@Column(nullable = false)
	private String address;
	
	
	private String description;




	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}

}
