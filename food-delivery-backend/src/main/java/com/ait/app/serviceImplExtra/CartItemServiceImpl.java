package com.ait.app.serviceImplExtra;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.CartItemDto;
import com.ait.app.dto.CartItemDto2;
import com.ait.app.entity.Cart;
import com.ait.app.entity.CartItem;
import com.ait.app.entity.FoodItem;
import com.ait.app.exception.CartItemServiceException;
import com.ait.app.repository.CartItemRepository;
import com.ait.app.repository.CartRepository;
import com.ait.app.repository.FooditemRepo;
import com.ait.app.service.CartItemService;

@Service
public class CartItemServiceImpl implements CartItemService {

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private FooditemRepo foodRepo;

	@Override
	public void saveCartItem(CartItemDto dto) {

		Optional<Cart> cartOptional = cartRepository.findById(dto.getCartId());

		if (cartOptional.isEmpty()) {
			throw new CartItemServiceException("Cart not found", HttpStatus.NOT_FOUND);
		}

		Optional<FoodItem> foodItemOptional = foodRepo.findById(dto.getFoodItemId());

		if (foodItemOptional.isEmpty()) {
			throw new CartItemServiceException("Food item not found", HttpStatus.NOT_FOUND);
		}

		if (dto.getQuantity() < 1) {
			throw new CartItemServiceException("Quantity must be at least 1", HttpStatus.BAD_REQUEST);
		}

		Cart cart = cartOptional.get();
		FoodItem foodItem = foodItemOptional.get();

		List<CartItem> existingItems = cartItemRepository.findByCartId(dto.getCartId());

		for (CartItem item : existingItems) {

			if (item.getFoodItem().getFoodid() == dto.getFoodItemId()) {

				throw new CartItemServiceException("Food item already exists in cart", HttpStatus.CONFLICT);
			}
		}

		CartItem cartItem = new CartItem();

		cartItem.setCart(cart);
		cartItem.setFoodItem(foodItem);
		cartItem.setQuantity(dto.getQuantity());

		double unitPrice = foodItem.getPrice();
		double subtotal = unitPrice * dto.getQuantity();

		cartItem.setUnitPrice(unitPrice);
		cartItem.setSubtotal(subtotal);

		cartItemRepository.save(cartItem);

		updateCartTotal(cart);
	}

	@Override
	public CartItemDto2 getCartItem(int id) {

		Optional<CartItem> optional = cartItemRepository.findById(id);

		if (optional.isEmpty()) {
			throw new CartItemServiceException("Cart item not found", HttpStatus.NOT_FOUND);
		}

		return convertToDto(optional.get());
	}

	@Override
	public List<CartItemDto2> getAllCartItems() {

		List<CartItem> cartItems = cartItemRepository.findAll();

		List<CartItemDto2> list = new ArrayList<>();

		for (CartItem cartItem : cartItems) {
			list.add(convertToDto(cartItem));
		}

		return list;
	}

	@Override
	public void deleteCartItem(int id) {

		Optional<CartItem> optional = cartItemRepository.findById(id);

		if (optional.isEmpty()) {
			throw new CartItemServiceException("Cart item not found", HttpStatus.NOT_FOUND);
		}

		Cart cart = optional.get().getCart();

		cartItemRepository.deleteById(id);

		updateCartTotal(cart);
	}

	@Override
	public void deleteAllCartItems() {

		List<CartItem> cartItems = cartItemRepository.findAll();

		if (cartItems.isEmpty()) {
			throw new CartItemServiceException("Cart items not found", HttpStatus.NOT_FOUND);
		}

		cartItemRepository.deleteAll();
	}

	private void updateCartTotal(Cart cart) {

		List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

		double total = 0.0;

		for (CartItem item : cartItems) {
			total += item.getSubtotal();
		}

		cart.setTotalAmount(total);

		cartRepository.save(cart);
	}

	private CartItemDto2 convertToDto(CartItem cartItem) {

		CartItemDto2 dto = new CartItemDto2();

		dto.setFoodItemId(cartItem.getFoodItem().getFoodid());
		
		dto.setFoodname(cartItem.getFoodItem().getFoodname());

		dto.setQuantity(cartItem.getQuantity());

		dto.setUnitPrice(cartItem.getUnitPrice());

		dto.setSubtotal(cartItem.getSubtotal());

		return dto;
	}
}