package com.ait.app.dto;

import lombok.Data;

@Data
public class DelieveryPricingRuleDto {
	
	
	private double basefees;
	private double perKmRate;
	private double maxdelieveryradius;
	private double freeDelievery;
	private boolean active;


}
