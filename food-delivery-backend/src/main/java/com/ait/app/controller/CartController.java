package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.CartResponse;
import com.ait.app.entity.Cart;
import com.ait.app.service.CartService;

@RestController
public class CartController {

	@Autowired
	CartService cartService;

	@PostMapping("/addCart")
	public ResponseEntity addCart(@RequestBody CartResponse dto) {

		cartService.saveCart(dto);

		return new ResponseEntity("Cart added successfully", HttpStatus.CREATED);
	}

	@DeleteMapping("/deleteCart/{id}")
	public ResponseEntity deleteCart(@PathVariable int id) {

		cartService.deleteCart(id);

		return new ResponseEntity("Cart deleted successfully", HttpStatus.OK);
	}

	@GetMapping("/getCart/{id}")
	public ResponseEntity<Cart> getCart(@PathVariable int id) {

		Cart cart = cartService.getCart(id);

		return new ResponseEntity(cart, HttpStatus.OK);
	}

}
