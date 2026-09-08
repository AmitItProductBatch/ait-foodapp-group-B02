package com.ait.app.service;

import com.ait.app.dto.FoodItemDto;
import com.ait.app.entity.FoodItem;

public interface Fooditemservice {
	
  FoodItem addfooditem(FoodItemDto dto);
  FoodItem getfooditem(int id);
  void deletefooditem(int id);
	

}
