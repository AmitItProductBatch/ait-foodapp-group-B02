package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.DelieveryPricingRuleDto;
import com.ait.app.service.DelieveryPricingRule;


@RestController
@RequestMapping("/delieverypricing")
public class DelieveryPricingRuleController {
	
	
	@Autowired
	DelieveryPricingRule delieveryPricingRule;
	
	
	@PostMapping("/add")
	ResponseEntity<DelieveryPricingRuleDto> addDeliveryPricingRule(DelieveryPricingRuleDto delieveryPricingRuleDto) {
		delieveryPricingRule.addDeliveryPricingRule(delieveryPricingRuleDto);
		return new ResponseEntity("delievery Pricing Rule added",HttpStatus.CREATED);
	}

}
