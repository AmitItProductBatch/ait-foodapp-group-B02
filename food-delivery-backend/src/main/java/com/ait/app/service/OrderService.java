package com.ait.app.service;

import java.util.List;

import com.ait.app.dto.OrderRequestDto;
import com.ait.app.dto.OrderResponseDto;


public interface OrderService {

	OrderResponseDto createOrder(OrderRequestDto order);

	List<OrderResponseDto> getAllOrders();

	OrderResponseDto getOrderById(int id);

	OrderResponseDto updateOrder(int id, String status);

	void cancelOrder(int id);
}