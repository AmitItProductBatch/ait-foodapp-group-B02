package com.ait.app.serviceImplExtra;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.FoodItemDto;
import com.ait.app.entity.FoodItem;
import com.ait.app.exception.FooditemException;
import com.ait.app.repository.FooditemRepo;
import com.ait.app.service.Fooditemservice;

@Service
public class FoodItemServiceImpl implements Fooditemservice{
	
	@Autowired
	FooditemRepo fooditemRepo;

	@Override
	public FoodItem addfooditem(FoodItemDto dto) {
		if(dto==null) {
			throw new FooditemException("fooditem not found", HttpStatus.BAD_REQUEST);
			    }
		
		Optional<FoodItem> existingfood=fooditemRepo.findByFoodname(dto.getFoodname());
		if(existingfood.isPresent()) {
			throw new FooditemException("food already exist", HttpStatus.BAD_REQUEST);
		}
			FoodItem item = new FoodItem();
	         item.setFoodname(dto.getFoodname());
	         item.setFoodtype(dto.getFoodtype());
	         item.setDescription(dto.getDescription());
	         item.setCuisine(dto.getCuisine());
	         item.setAvailable(false);
			
			return	fooditemRepo.save(item);
	}

	@Override
	public FoodItem getfooditem(int id) {
	Optional<FoodItem>	o=fooditemRepo.findById(id);
		if(o.isEmpty()) {
			throw new FooditemException("food item is empty", HttpStatus.BAD_REQUEST);
		}
		return o.get();
	}

	@Override
	public void deletefooditem(int id) {
		
		fooditemRepo.deleteById(id);
	}

	@Override
	public FoodItemDto updateFoodItem(int id, FoodItemDto dto) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
