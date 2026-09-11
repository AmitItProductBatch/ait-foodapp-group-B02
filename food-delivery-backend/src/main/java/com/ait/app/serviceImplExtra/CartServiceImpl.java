package com.ait.app.serviceImplExtra;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.CartResponse;
import com.ait.app.entity.Cart;
import com.ait.app.entity.User;
import com.ait.app.repository.CartRepository;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	CartRepository cartRepository;

	@Autowired
	UserRepository userRepository;

	@Override
	public void saveCart(CartResponse dto) {

		int userId = dto.getUserId();

		User user = userRepository.findById(userId).get();
		Cart cart = new Cart();
		cart.setUser(user);

		cartRepository.save(cart);
	}

	@Override
	public void deleteCart(int id) {

		cartRepository.deleteById(id);
	}

	@Override
	public Cart getCart(int id) {

		Cart cart = cartRepository.findById(id).get();
		return cart;
	}

	@Override
	public Cart updateCart(int id, Cart cart) {

		return null;
	}

	@Override
	public List<Cart> getAllCart() {

		List<Cart> list = cartRepository.findAll();

		return list;
	}

}
