package com.ait.app.dto;

import org.springframework.stereotype.Service;

import jakarta.persistence.Column;

@Service
public class RestaurantRequestBody {
	

	private String name;
	
	
	private String phone;
	
	
	private String email;
	
 
	
	
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


	 


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}

}
