package com.ait.app.serviceImplExtra;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.CartItemDto2;
import com.ait.app.dto.CartResponse;
import com.ait.app.dto.CartResponse2;
import com.ait.app.dto.CartResponse3;
import com.ait.app.entity.Cart;
import com.ait.app.entity.CartItem;
import com.ait.app.entity.Restaurant;
import com.ait.app.entity.User;
import com.ait.app.exception.CartServiceException;
import com.ait.app.repository.CartRepository;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

	@Autowired
	CartRepository cartRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RestaurantRepository restaurantRepository;

	@Override
	public void saveCart(CartResponse dto) {

		log.info("Creating cart for user");

		int userId = dto.getUserId();
		if (userId <= 0) {

			log.warn("Invalid user id: {}", userId);
			throw new CartServiceException("Invalid user id", HttpStatus.BAD_REQUEST);
		}

		Optional<User> uo = userRepository.findById(userId);

		if (uo.isEmpty()) {

			log.warn("User not found with id: {}", userId);
			throw new CartServiceException("User not found", HttpStatus.NOT_FOUND);
		}

		if (cartRepository.existsByUserId(userId)) {

			log.warn("Cart already exists for user id: {}", userId);
			throw new CartServiceException("Cart already exists for this user", HttpStatus.CONFLICT);
		}

		long restaurentId = dto.getRestaurentId();

		if (restaurentId <= 0) {

			log.warn("Invalid restaurant id: {}", restaurentId);
			throw new CartServiceException("Invalid restaurant id", HttpStatus.BAD_REQUEST);
		}

		Optional<Restaurant> ro = restaurantRepository.findById(restaurentId);

		if (ro.isEmpty()) {

			log.warn("Restaurant not found with id: {}", restaurentId);
			throw new CartServiceException("Restaurent not Found", HttpStatus.NOT_FOUND);
		}

		User user = uo.get();
		Restaurant restaurant = ro.get();

		Cart cart = new Cart();

		cart.setUser(user);
		cart.setRestaurant(restaurant);
		cart.setTotalAmount(0.0);

		cartRepository.save(cart);

		log.info("Cart created successfully for user id: {} and restaurant id: {}", userId, restaurentId);
	}

	@Override
	public void deleteCart(int id) {

		log.info("Deleting cart with id: {}", id);

		if (!cartRepository.existsById(id)) {

			log.warn("Cart not found with id: {}", id);
			throw new CartServiceException("Cart not found", HttpStatus.NOT_FOUND);
		}

		cartRepository.deleteById(id);

		log.info("Cart deleted successfully with id: {}", id);
	}

	@Override
	public CartResponse2 getCart(int userId) {

		log.info("Getting cart for user id: {}", userId);

		if (userId <= 0) {

			log.warn("Invalid user id: {}", userId);
			throw new CartServiceException("Invalid user id", HttpStatus.BAD_REQUEST);
		}

		Optional<Cart> o = cartRepository.findByUserId(userId);

		if (o.isEmpty()) {

			log.warn("Cart not found for user id: {}", userId);
			throw new CartServiceException("Cart not found", HttpStatus.NOT_FOUND);
		}

		Cart cart = o.get();

		CartResponse2 dto = new CartResponse2();

		List<CartItemDto2> items = new ArrayList();

		if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {

			log.warn("No cart items found for cart id: {}", cart.getId());
			throw new CartServiceException("No cart items found for this cart", HttpStatus.NOT_FOUND);
		}

		for (CartItem item : cart.getCartItems()) {

			if (item.getFoodItem() == null) {

				log.warn("Food item not found for cart item id: {}", item.getId());
				throw new CartServiceException("Food item not found for this cart item", HttpStatus.NOT_FOUND);
			}
			
			CartItemDto2 cartItemDto2 = new CartItemDto2();
			cartItemDto2.setFoodItemId(item.getFoodItem().getFoodid());
			cartItemDto2.setFoodname(item.getFoodItem().getFoodname());
			cartItemDto2.setQuantity(item.getQuantity());
			cartItemDto2.setUnitPrice(item.getUnitPrice());
			cartItemDto2.setSubtotal(item.getSubtotal());
			items.add(cartItemDto2);
		}

		dto.setRestaurantName(cart.getRestaurant().getName());
		dto.setItems(items);
		dto.setTotalAmount(cart.getTotalAmount());

		log.info("Cart fetched successfully for user id: {}", userId);

		return dto;
	}

	@Override
	public Cart updateCart(int id, Cart cart) {

		return null;
	}

	@Override
	public List<CartResponse3> getAllCart() {

		log.info("Getting all carts");

		List<Cart> list = cartRepository.findAll();

		if (list.isEmpty()) {

			log.warn("No carts found");
			throw new CartServiceException("Carts is emptyy", HttpStatus.NOT_FOUND);
		}

		List<CartResponse3> l2 = new ArrayList();

		for (Cart cart : list) {

			CartResponse3 dto = new CartResponse3();

			if (cart.getRestaurant() == null) {

				log.warn("Restaurant not found for cart id: {}", cart.getId());
				throw new CartServiceException("Restaurant not found for this cart", HttpStatus.NOT_FOUND);
			}

			if (cart.getUser() == null) {

				log.warn("User not found for cart id: {}", cart.getId());
				throw new CartServiceException("User not found for this cart", HttpStatus.NOT_FOUND);
			}

			dto.setRestaurantName(cart.getRestaurant().getName());
			dto.setUserName(cart.getUser().getName());
			dto.setUserMobile(cart.getUser().getMobile());

			List<CartItemDto2> items = new ArrayList<>();

			if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {

				log.warn("No cart items found for cart id: {}", cart.getId());
				throw new CartServiceException("No cart items found for this cart", HttpStatus.NOT_FOUND);
			}

			for (CartItem item : cart.getCartItems()) {

				if (item.getFoodItem() == null) {

					log.warn("Food item not found for cart item id: {}", item.getId());
					throw new CartServiceException("Food item not found for this cart item", HttpStatus.NOT_FOUND);
				}

				CartItemDto2 itemDto = new CartItemDto2();

				itemDto.setFoodItemId(item.getFoodItem().getFoodid());
				itemDto.setFoodname(item.getFoodItem().getFoodname());
				itemDto.setQuantity(item.getQuantity());
				itemDto.setUnitPrice(item.getUnitPrice());
				itemDto.setSubtotal(item.getSubtotal());
				items.add(itemDto);
			}

			dto.setItems(items);
			dto.setTotalAmount(cart.getTotalAmount());

			l2.add(dto);
		}

		log.info("Successfully fetched {} carts", l2.size());

		return l2;
	}
}
