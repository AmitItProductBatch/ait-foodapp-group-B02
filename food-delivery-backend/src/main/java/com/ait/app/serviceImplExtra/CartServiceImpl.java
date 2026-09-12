package com.ait.app.serviceImplExtra;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.CartResponse;
import com.ait.app.entity.Cart;
import com.ait.app.entity.Restaurant;
import com.ait.app.entity.User;
import com.ait.app.exception.CartServiceException;
import com.ait.app.repository.CartRepository;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	CartRepository cartRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RestaurantRepository restaurantRepository;

	@Override
	public void saveCart(CartResponse dto) {

		int userId = dto.getUserId();
		Optional<User> uo = userRepository.findById(userId);

		if (uo.isEmpty()) {

			throw new CartServiceException("User not found", HttpStatus.NOT_FOUND);
		}

		if (cartRepository.existsByUserId(userId)) {

			throw new CartServiceException("Cart already exists for this user", HttpStatus.CONFLICT);
		}

		long restaurentId = dto.getRestaurentId();
		Optional<Restaurant> ro = restaurantRepository.findById(restaurentId);

		if (ro.isEmpty()) {

			throw new CartServiceException("Restaurent not Found", HttpStatus.NOT_FOUND);
		}

		User user = uo.get();
		Restaurant restaurant = ro.get();

		Cart cart = new Cart();
		cart.setUser(user);
		cart.setRestaurent(restaurant);

		cartRepository.save(cart);
	}

	@Override
	public void deleteCart(int id) {

		if (userRepository.existsById(id)) {

			cartRepository.deleteById(id);
		}

		throw new CartServiceException("Cart not found", HttpStatus.NOT_FOUND);
	}

	@Override
	public Cart getCart(int id) {

		Optional<Cart> o = cartRepository.findById(id);

		if (o.isEmpty()) {

			throw new CartServiceException("Cart not found", HttpStatus.NOT_FOUND);
		}

		return o.get();
	}

	@Override
	public Cart updateCart(int id, Cart cart) {

		return null;
	}

	@Override
	public List<Cart> getAllCart() {

		List<Cart> list = cartRepository.findAll();

		if (list.isEmpty()) {

			throw new CartServiceException("Carts is emptyy", HttpStatus.NOT_FOUND);
		}

		return list;
	}

}
