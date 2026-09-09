package com.ait.app.service;

import java.util.List;

import com.ait.app.dto.CartResponse;
import com.ait.app.entity.Cart;

public interface CartService {

	public void saveCart(CartResponse dto);

	public void deleteCart(int id);

	Cart getCart(int id);

	Cart updateCart(int id, Cart cart);

	List<Cart> getAllCart();

}
