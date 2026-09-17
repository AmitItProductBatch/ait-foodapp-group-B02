package com.ait.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.CartResponse;
import com.ait.app.dto.CartResponse2;
import com.ait.app.dto.CartResponse3;
import com.ait.app.entity.Cart;
import com.ait.app.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	@Autowired
	CartService cartService;

	@PostMapping
	public ResponseEntity addCart(@RequestBody CartResponse dto) {

		cartService.saveCart(dto);

		return new ResponseEntity("Cart added successfully", HttpStatus.CREATED);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity deleteCart(@PathVariable int id) {

		cartService.deleteCart(id);

		return new ResponseEntity("Cart deleted successfully", HttpStatus.OK);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<Cart> getCart(@PathVariable int userId) {

		CartResponse2 cart = cartService.getCart(userId);

		return new ResponseEntity(cart, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity getAllCart() {

		List<CartResponse3> list = cartService.getAllCart();

		return new ResponseEntity(list, HttpStatus.OK);
	}
}
