package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.PriceResponse;
import com.ait.app.service.PriceService;

@RestController
public class PriceController {

	@Autowired
	PriceService priceService;
	
	@GetMapping("/{itemId}")
	public ResponseEntity getPrice(@PathVariable int itemId) {
		
		PriceResponse p = priceService.getPrice(itemId);
		
		return new ResponseEntity(p, HttpStatus.OK);
	}
}
