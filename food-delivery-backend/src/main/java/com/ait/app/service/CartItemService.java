package com.ait.app.service;

import java.util.List;

import com.ait.app.dto.CartItemDto;
import com.ait.app.dto.CartItemDto2;

public interface CartItemService {

    void saveCartItem(CartItemDto dto);

    CartItemDto2 getCartItem(int id);

    List<CartItemDto2> getAllCartItems();

    void deleteCartItem(int id);

    void deleteAllCartItems();
}