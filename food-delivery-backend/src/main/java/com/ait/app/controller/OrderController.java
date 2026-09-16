package com.ait.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ait.app.dto.OrderRequestDto;
import com.ait.app.dto.OrderResponseDto;
import com.ait.app.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	@Autowired
	OrderService orderService;

	// CREATE ORDER
	@PostMapping
	public ResponseEntity<String> createOrder(@RequestBody OrderRequestDto dto) {

		orderService.createOrder(dto);

		return new ResponseEntity<>("Order successfully created", HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<OrderResponseDto>> getAllviewOrders() {

		List<OrderResponseDto> orders = orderService.getAllOrders();

		return new ResponseEntity<>(orders, HttpStatus.OK);
	}


	@GetMapping("/{id}")
	public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable int id) {

		OrderResponseDto order = orderService.getOrderById(id);

		return new ResponseEntity<>(order, HttpStatus.OK);
	}

	@PutMapping("/{id}")
	public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable int id, @RequestParam String status) {

		OrderResponseDto order = orderService.updateOrder(id, status);

		return new ResponseEntity<>(order, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> cancelOrder(@PathVariable int id) {

		orderService.cancelOrder(id);

		return new ResponseEntity<>("Order cancelled successfully", HttpStatus.OK);
	}
}