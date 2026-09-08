package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.FoodItemDto;
import com.ait.app.entity.FoodItem;
import com.ait.app.service.Fooditemservice;

@RestController
@RequestMapping("/Food")
public class FooditemController {
	
	@Autowired
	Fooditemservice fooditemservice;
	
	@PostMapping("/add")
	 ResponseEntity addfooditem(@RequestBody FoodItemDto dto) {
		
		fooditemservice.addfooditem(dto);
		
		return new ResponseEntity("fooditem added",HttpStatus.CREATED);
		
	 }
	
	@GetMapping("/{id}")
	ResponseEntity<FoodItem> getfooditem(@PathVariable  int id) {
		
	FoodItem f=	fooditemservice.getfooditem(id);
		return new ResponseEntity(f,HttpStatus.OK);
		
		
	}
	 @DeleteMapping("/delete/{id}")
	void deletefooditem(int id) {
		 fooditemservice.deletefooditem(id);
	}
}
