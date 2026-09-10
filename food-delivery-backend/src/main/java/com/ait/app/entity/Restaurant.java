package com.ait.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "restaurants")
public class Restaurant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false, unique = true)
	private String phone;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	
	private String description;
	

	@OneToOne(mappedBy = "restaurant")
	private RestaurantAddress restaurantAddress;
	
	@OneToMany(mappedBy = "restaurant")
	private List<FoodItem> foodItems;
	


	public RestaurantAddress getRestaurantAddress() {
		return restaurantAddress;
	}


	public void setRestaurantAddress(RestaurantAddress restaurantAddress) {
		this.restaurantAddress = restaurantAddress;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


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
