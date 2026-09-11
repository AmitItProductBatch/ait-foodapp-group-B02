package com.ait.app.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "restaurants")
@Data
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

	@OneToMany(mappedBy = "restaurant",cascade = CascadeType.ALL)
	private List<Cart> carts;

}
