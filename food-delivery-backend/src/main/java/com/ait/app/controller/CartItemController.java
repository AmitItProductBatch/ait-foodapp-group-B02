package com.ait.app.controller;

import java.util.List;

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

import com.ait.app.dto.CartItemDto;
import com.ait.app.dto.CartItemDto2;
import com.ait.app.service.CartItemService;

@RestController
@RequestMapping("/api/cart/items")
public class CartItemController {

	@Autowired
	private CartItemService cartItemService;

	@PostMapping
	public ResponseEntity<String> saveCartItem(@RequestBody CartItemDto dto) {

		cartItemService.saveCartItem(dto);

		return new ResponseEntity<>("Cart item added successfully", HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<CartItemDto2> getCartItem(@PathVariable int id) {

		CartItemDto2 cartItem = cartItemService.getCartItem(id);

		return new ResponseEntity<>(cartItem, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<CartItemDto2>> getAllCartItems() {

		List<CartItemDto2> cartItems = cartItemService.getAllCartItems();

		return new ResponseEntity<>(cartItems, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteCartItem(@PathVariable int id) {

		cartItemService.deleteCartItem(id);

		return new ResponseEntity<>("Cart item deleted successfully", HttpStatus.OK);
	}

	@DeleteMapping
	public ResponseEntity<String> deleteAllCartItems() {

		cartItemService.deleteAllCartItems();

		return new ResponseEntity<>("All cart items deleted successfully", HttpStatus.OK);
	}
}