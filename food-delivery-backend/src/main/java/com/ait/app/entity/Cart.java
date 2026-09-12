package com.ait.app.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

@Entity
@Data
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private double totalAmount = 0.0;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
		updatedAt = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
	}

	@PreUpdate
	public void onUpdate() {
		updatedAt = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
	}

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "restaurant_id")
	private Restaurant restaurant;

}
