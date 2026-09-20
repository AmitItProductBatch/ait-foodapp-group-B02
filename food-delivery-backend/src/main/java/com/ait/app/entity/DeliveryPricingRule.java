package com.ait.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class DeliveryPricingRule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int deliverypricingruleid;
	private double basefees;
	private double perKmRate;
	private double maxdelieveryradius;
	private double freeDelievery;
	private boolean active;

}
