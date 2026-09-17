
package com.ait.app.service;

import java.util.List;

import com.ait.app.dto.CartResponse;
import com.ait.app.dto.CartResponse2;
import com.ait.app.dto.CartResponse3;
import com.ait.app.entity.Cart;

public interface CartService {

	public void saveCart(CartResponse dto);

	public void deleteCart(int id);

	CartResponse2 getCart(int id);

	Cart updateCart(int id, Cart cart);

	List<CartResponse3> getAllCart();

}
