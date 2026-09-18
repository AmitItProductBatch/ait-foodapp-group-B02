package com.ait.app.serviceImplExtra;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import com.ait.app.dto.OrderItemResponseDto;
import com.ait.app.dto.OrderRequestDto;
import com.ait.app.dto.OrderResponseDto;
import com.ait.app.entity.Cart;
import com.ait.app.entity.CartItem;
import com.ait.app.entity.Order;
import com.ait.app.entity.OrderItem;
import com.ait.app.entity.Restaurant;
import com.ait.app.entity.User;
import com.ait.app.entity.UserAddress;
import com.ait.app.exception.OrderException;
import com.ait.app.repository.CartItemRepository;
import com.ait.app.repository.CartRepository;
import com.ait.app.repository.OrderRepository;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.repository.UserAddressRepository;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RestaurantRepository restaurantRepository;

	@Autowired
	UserAddressRepository userAddressRepository;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	CartItemRepository cartItemRepository;

	@Override
	@Transactional
	public OrderResponseDto createOrder(OrderRequestDto dto) {

		if (dto.getUserId() <= 0) {
			throw new OrderException("User Id is required", HttpStatus.BAD_REQUEST);
		}

		Optional<User> userOptional = userRepository.findById(dto.getUserId());

		if (userOptional.isEmpty()) {
			throw new OrderException("User not found with id : " + dto.getUserId(), HttpStatus.NOT_FOUND);
		}

		User user = userOptional.get();

		Optional<Cart> cartOptional = cartRepository.findByUserId(dto.getUserId());

		if (cartOptional.isEmpty()) {
			throw new OrderException("Cart not found for user", HttpStatus.NOT_FOUND);
		}

		Cart cart = cartOptional.get();

		List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

		if (cartItems == null || cartItems.isEmpty()) {
			throw new OrderException("Cart is empty", HttpStatus.BAD_REQUEST);
		}

		Restaurant restaurant = cart.getRestaurant();

		if (restaurant == null) {
			throw new OrderException("Restaurant not found in cart", HttpStatus.NOT_FOUND);
		}

		if (restaurant.isOpen() == false) {
			throw new OrderException("Restaurant is closed", HttpStatus.BAD_REQUEST);
		}

		Optional<UserAddress> userAddressOptional = userAddressRepository.findById(dto.getDeliveryAddressId());

		if (userAddressOptional.isEmpty()) {
			throw new OrderException("Delivery address not found", HttpStatus.NOT_FOUND);
		}

		UserAddress userAddress = userAddressOptional.get();

		if (userAddress.getUser().getId() != user.getId()) {
			throw new OrderException("Delivery address does not belong to this user", HttpStatus.BAD_REQUEST);
		}

		Order order = new Order();

		order.setUser(user);
		order.setRestaurant(restaurant);

		String deliveryAddress = userAddress.getHouseNo() + ", " + userAddress.getBuildingName() + ", "
				+ userAddress.getStreet() + ", " + userAddress.getArea() + ", " + userAddress.getCity() + ", "
				+ userAddress.getState() + " - " + userAddress.getPincode();

		order.setDeliveryAddress(deliveryAddress);

		order.setPaymentMethod(dto.getPaymentMethod());
		order.setOrderStatus("PLACED");
		order.setPaymentStatus("PENDING");
		order.setCreatedAt(LocalDateTime.now());

		List<OrderItem> orderItems = new ArrayList<OrderItem>();

		BigDecimal subtotal = BigDecimal.ZERO;

		for (CartItem cartItem : cartItems) {

			OrderItem orderItem = new OrderItem();

			orderItem.setOrder(order);

			orderItem.setFoodItem(cartItem.getFoodItem());

			orderItem.setItemName(cartItem.getFoodItem().getFoodname());

			BigDecimal unitPrice = BigDecimal.valueOf(cartItem.getUnitPrice());

			BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

			orderItem.setUnitPrice(unitPrice);

			orderItem.setQuantity(cartItem.getQuantity());

			orderItem.setTotalPrice(totalPrice);

			orderItems.add(orderItem);

			subtotal = subtotal.add(totalPrice);
		}

		order.setOrderItems(orderItems);

		order.setSubtotal(subtotal);

		BigDecimal discount = BigDecimal.ZERO;

		BigDecimal tax = BigDecimal.ZERO;

		BigDecimal deliveryFee = BigDecimal.ZERO;

		BigDecimal packagingFee = BigDecimal.ZERO;

		order.setTax(tax);
		order.setDeliveryFee(deliveryFee);
		order.setPackagingFee(packagingFee);

		BigDecimal totalAmount = subtotal.subtract(discount).add(tax).add(deliveryFee).add(packagingFee);

		order.setTotalAmount(totalAmount);

		Order savedOrder = orderRepository.save(order);

		cartItemRepository.deleteByCartId(cart.getId());
		cart.setTotalAmount(0.0);
		cartRepository.save(cart);

		OrderResponseDto response = convertToResponseDto(savedOrder);

		response.setDiscount(discount);

		return response;
	}

	@Override
	public List<OrderResponseDto> getAllOrders() {

		List<Order> orders = orderRepository.findAll();

		List<OrderResponseDto> responseListorders = new ArrayList<OrderResponseDto>();

		for (Order order : orders) {

			OrderResponseDto response = convertToResponseDto(order);

			responseListorders.add(response);
		}

		return responseListorders;
	}

	@Override
	public OrderResponseDto getOrderById(int id) {

		if (id <= 0) {
			throw new OrderException("Invalid Order Id", HttpStatus.BAD_REQUEST);
		}

		Optional<Order> orderOptional = orderRepository.findById(id);

		if (orderOptional.isEmpty()) {
			throw new OrderException("Order not found with id : " + id, HttpStatus.NOT_FOUND);
		}

		Order order = orderOptional.get();

		OrderResponseDto response = convertToResponseDto(order);

		return response;
	}

	@Override
	public OrderResponseDto updateOrder(int id, String status) {

		if (id <= 0) {
			throw new OrderException("Invalid Order Id", HttpStatus.BAD_REQUEST);
		}

		if (status == null || status.isEmpty()) {
			throw new OrderException("Order status cannot be empty", HttpStatus.BAD_REQUEST);
		}

		Optional<Order> orderOptional = orderRepository.findById(id);

		if (orderOptional.isEmpty()) {
			throw new OrderException("Order not found with id : " + id, HttpStatus.NOT_FOUND);
		}

		Order order = orderOptional.get();

		order.setOrderStatus(status);

		Order updatedOrder = orderRepository.save(order);

		OrderResponseDto response = convertToResponseDto(updatedOrder);

		return response;
	}

	@Override
	public void cancelOrder(int id) {

		if (id <= 0) {
			throw new OrderException("Invalid Order Id", HttpStatus.BAD_REQUEST);
		}

		Optional<Order> orderOptional = orderRepository.findById(id);

		if (orderOptional.isEmpty()) {
			throw new OrderException("Order not found with id : " + id, HttpStatus.NOT_FOUND);
		}

		Order order = orderOptional.get();

		order.setOrderStatus("CANCELLED");

		orderRepository.save(order);
	}

	public OrderResponseDto convertToResponseDto(Order order) {

		OrderResponseDto response = new OrderResponseDto();

		response.setOrderId((long) order.getId());

		response.setUserId((long) order.getUser().getId());

		response.setRestaurantId(order.getRestaurant().getId());

		response.setSubtotal(order.getSubtotal());

		response.setTax(order.getTax());

		response.setDeliveryFee(order.getDeliveryFee());

		response.setPackagingFee(order.getPackagingFee());

		response.setTotalAmount(order.getTotalAmount());

		response.setOrderStatus(order.getOrderStatus());

		response.setPaymentStatus(order.getPaymentStatus());

		response.setCreatedAt(order.getCreatedAt());

		List<OrderItemResponseDto> itemResponseList = new ArrayList<OrderItemResponseDto>();

		if (order.getOrderItems() != null) {

			for (OrderItem item : order.getOrderItems()) {

				OrderItemResponseDto itemResponse = new OrderItemResponseDto();

				itemResponse.setMenuItemId((long) item.getFoodItem().getFoodid());

				itemResponse.setItemName(item.getItemName());

				itemResponse.setQuantity(item.getQuantity());

				itemResponse.setUnitPrice(item.getUnitPrice());

				itemResponse.setTotalPrice(item.getTotalPrice());

				itemResponseList.add(itemResponse);
			}
		}

		response.setItems(itemResponseList);

		return response;
	}
}