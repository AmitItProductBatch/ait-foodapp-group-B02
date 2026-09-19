package com.ait.app.serviceImplExtra;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger log = LoggerFactory.getLogger(CartItemServiceImpl.class);

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private FooditemRepo foodRepo;

	@Override
	public void saveCartItem(CartItemDto dto) {

		log.info("Saving cart item");

		Optional<Cart> cartOptional = cartRepository.findById(dto.getCartId());

		if (cartOptional.isEmpty()) {

			log.warn("Cart not found");
			throw new CartItemServiceException("Cart not found", HttpStatus.NOT_FOUND);
		}

		Optional<FoodItem> foodItemOptional = foodRepo.findById(dto.getFoodItemId());

		if (foodItemOptional.isEmpty()) {

			log.warn("Food item not found");
			throw new CartItemServiceException("Food item not found", HttpStatus.NOT_FOUND);
		}

		if (dto.getQuantity() < 1) {

			log.warn("Invalid quantity");
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

		log.info("Cart item saved successfully");
	}

	@Override
	public CartItemDto2 getCartItem(int id) {

		log.info("Getting cart item");
		Optional<CartItem> optional = cartItemRepository.findById(id);

		if (optional.isEmpty()) {

			log.warn("Cart item not found");
			throw new CartItemServiceException("Cart item not found", HttpStatus.NOT_FOUND);
		}

		log.info("Cart item fetched successfully");
		return convertToDto(optional.get());
	}

	@Override
	public List<CartItemDto2> getAllCartItems() {

		log.info("Getting all cart items");
		List<CartItem> cartItems = cartItemRepository.findAll();

		List<CartItemDto2> list = new ArrayList<>();

		for (CartItem cartItem : cartItems) {
			list.add(convertToDto(cartItem));
		}

		log.info("All cart items fetched successfully");

		return list;
	}

	@Override
	public void deleteCartItem(int id) {

		log.info("Deleting cart item");

		Optional<CartItem> optional = cartItemRepository.findById(id);

		if (optional.isEmpty()) {

			log.warn("Cart item not found");

			throw new CartItemServiceException("Cart item not found", HttpStatus.NOT_FOUND);
		}

		Cart cart = optional.get().getCart();

		cartItemRepository.deleteById(id);

		updateCartTotal(cart);

		log.info("Cart item deleted successfully");
	}

	@Override
	public void deleteAllCartItems() {

		log.info("Deleting all cart items");
		List<CartItem> cartItems = cartItemRepository.findAll();

		if (cartItems.isEmpty()) {

			log.warn("No cart items found");
			throw new CartItemServiceException("Cart items not found", HttpStatus.NOT_FOUND);
		}

		cartItemRepository.deleteAll();
		log.info("All cart items deleted successfully");
	}

	private void updateCartTotal(Cart cart) {

		log.info("Updating cart total");

		List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

		double total = 0.0;

		for (CartItem item : cartItems) {
			total += item.getSubtotal();
		}

		cart.setTotalAmount(total);

		cartRepository.save(cart);
		log.info("Cart total updated successfully");

	}

	private CartItemDto2 convertToDto(CartItem cartItem) {

		CartItemDto2 dto = new CartItemDto2();

		dto.setCartItemId(cartItem.getId());
		if (cartItem.getCart() != null) {
			dto.setCartId(cartItem.getCart().getId());
		}

		if (cartItem.getFoodItem() != null) {
			dto.setFoodItemId(cartItem.getFoodItem().getFoodid());
			dto.setFoodname(cartItem.getFoodItem().getFoodname());
		}

		dto.setQuantity(cartItem.getQuantity());

		dto.setUnitPrice(cartItem.getUnitPrice());

		dto.setSubtotal(cartItem.getSubtotal());

		return dto;
	}
}