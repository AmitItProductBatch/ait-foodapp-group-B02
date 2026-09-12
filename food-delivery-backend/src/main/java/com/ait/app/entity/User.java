package com.ait.app.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	private long mobile;
	private String email;
	private String role;
	private String password;
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	private Cart cart;

	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
	private List<Feedback> feedbackList;
	
}