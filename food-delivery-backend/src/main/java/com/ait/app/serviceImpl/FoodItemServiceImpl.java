package com.ait.app.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.FoodItemDto;
import com.ait.app.entity.FoodItem;
import com.ait.app.entity.Restaurant;
import com.ait.app.exception.FooditemException;
import com.ait.app.repository.FooditemRepo;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.service.Fooditemservice;

@Service
public class FoodItemServiceImpl implements Fooditemservice{
	
	@Autowired
	FooditemRepo fooditemRepo;
	
	@Autowired
	RestaurantRepository repository;

	@Override
	public FoodItem addfooditem(FoodItemDto dto) {
		
	
		if(dto==null) {
			throw new FooditemException("fooditem not found", HttpStatus.BAD_REQUEST);
			    }
		
		
		if(dto.getFoodname()==null || dto.getFoodname().trim().isEmpty()) {
			throw new FooditemException("Food name cannot be empty",
                    HttpStatus.BAD_REQUEST);
			
		}
		
		
		if(dto.getFoodtype()==null || dto.getFoodtype().trim().isEmpty()) {
			throw new FooditemException( "Food type must be VEG or NON_VEG",
                    HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getDescription()==null || dto.getFoodtype().trim().isEmpty()) {
			throw new FooditemException("Description can not be empty", HttpStatus.BAD_REQUEST);
		}
		
		//validate cuisine
		if(dto.getCuisine()==null || dto.getCuisine().trim().isEmpty()) {
			throw new FooditemException("cuisine can not be empty", HttpStatus.BAD_REQUEST);
		}
		//validate restaurant id
		if(dto.getRestaurantId()<=0) {
			throw new FooditemException("Restaurant id must be greater than 0", HttpStatus.BAD_REQUEST);
		}
		//Check restaurant exists
		Restaurant restaurant = repository.findById((long) dto.getRestaurantId()).orElse(null);

		if (restaurant == null) {
		    throw new FooditemException(
		        "Restaurant not found with id: " + dto.getRestaurantId(),
		        HttpStatus.NOT_FOUND
		    );
		}
    		
		
		Optional<FoodItem> existingfood=fooditemRepo.findByFoodnameIgnoreCaseAndRestaurantId(dto.getFoodname().trim(),dto.getRestaurantId());
		if(existingfood.isPresent()) {
			throw new FooditemException("food already exist", HttpStatus.BAD_REQUEST);
		}
			FoodItem item = new FoodItem();
	         item.setFoodname(dto.getFoodname());
	         item.setFoodtype(dto.getFoodtype());
	         item.setDescription(dto.getDescription());
	         item.setCuisine(dto.getCuisine());
	         item.setAvailable(dto.isAvailable());
	         item.setPrice(dto.getPrice());
	         item.setRestaurant(restaurant);
			
			return	fooditemRepo.save(item);
	}

	@Override
	public FoodItem getfooditem(int id) {
		if(id<=0) {
			throw new FooditemException("food item id must be great", HttpStatus.BAD_REQUEST);
		}
	Optional<FoodItem>	o=fooditemRepo.findById(id);
		if(o.isEmpty()) {
			throw new FooditemException("Food item not found with id: " + id, HttpStatus.BAD_REQUEST);
		}
		return o.get();
	}

	@Override
	public void deletefooditem(int id) {
		if(id<=0) {
			throw new FooditemException("food item must be greater than 0", HttpStatus.BAD_REQUEST);
		}
	
		if(!fooditemRepo.existsById(id)) {
			throw new FooditemException("food item not found with id:"+id, HttpStatus.NOT_FOUND);
		}
		
		fooditemRepo.deleteById(id);
	}
	
	



	@Override
	public FoodItemDto updateFoodItem(int id, FoodItemDto dto) {
		// Validate ID
		if(id<=0) {
			throw new FooditemException("food item ID must be greater than 0", HttpStatus.BAD_REQUEST);
		}
		//validate dto
		if(dto==null) {
			throw new FooditemException(  "Food item details cannot be null", HttpStatus.BAD_REQUEST);
		}
		//Find existing food item
		FoodItem item = fooditemRepo.findById(id).orElse(null);
		if(item==null) {
			throw new FooditemException("Food item not found with id: " + id, HttpStatus.NOT_FOUND);
		}
		//  Validate food name
        if (dto.getFoodname() == null || dto.getFoodname().trim().isEmpty()) {
            throw new FooditemException(  "Food name cannot be empty", HttpStatus.BAD_REQUEST);
        }
        //  Validate food type
        if (dto.getFoodtype() == null ||dto.getFoodtype().trim().isEmpty()) {
            throw new FooditemException( "Food type cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (!dto.getFoodtype().equalsIgnoreCase("VEG") && !dto.getFoodtype().equalsIgnoreCase("NON_VEG")) {

            throw new FooditemException( "Food type must be VEG or NON_VEG", HttpStatus.BAD_REQUEST);
        }
       //validate description
        if(dto.getDescription()==null || dto.getDescription().trim().isEmpty()) {
        	throw new FooditemException("Description can not be empty", HttpStatus.BAD_REQUEST);
        }
        //validate cuisine
        if(dto.getCuisine()==null || dto.getCuisine().trim().isEmpty()) {
        	throw new FooditemException("cuisine can not be empty", HttpStatus.BAD_REQUEST);
        }
        //validate price
        if(dto.getPrice()<=0) {
        	throw new FooditemException("price must be greater than 0", HttpStatus.BAD_REQUEST);
        }
        //validate restaurant id
        if(dto.getRestaurantId()<=0) {
        	throw new FooditemException("Restaurant ID must be greater than 0", HttpStatus.BAD_REQUEST);
        }
        //find restaurant
        Restaurant rest = repository.findById((long)dto.getRestaurantId()).orElse(null);
        if(rest==null) {
        	throw new FooditemException("Restaurant not found with id:+id", HttpStatus.NOT_FOUND);
        }
        //check duplicate food name
        Optional<FoodItem> existingfood=fooditemRepo.findByFoodnameIgnoreCaseAndRestaurantId(dto.getFoodname(), dto.getRestaurantId());
		if(existingfood.isPresent()&&existingfood.get().getFoodid()!=id) {
			throw new FooditemException("food already exists in the restaurant", HttpStatus.CONFLICT);
		}
		//update field
		item.setFoodname(dto.getFoodname());
		item.setFoodtype(dto.getFoodtype());
		item.setDescription(dto.getDescription());
		item.setCuisine(dto.getCuisine());
		item.setPrice(dto.getPrice());
		item.setAvailable(dto.isAvailable());
		item.setRestaurant(rest);
		
		//update item
		FoodItem updateditem= fooditemRepo.save(item);
		//convert entity to dto
		FoodItemDto response= new FoodItemDto();
		response.setFoodname(updateditem.getFoodname());
		response.setFoodtype(updateditem.getFoodtype());
		response.setDescription(updateditem.getDescription());
		response.setCuisine(updateditem.getCuisine());
		response.setPrice(updateditem.getPrice());
		response.setAvailable(updateditem.isAvailable());
		response.setRestaurantId(updateditem.getRestaurant().getId().intValue());
        return response;
	}

	@Override
	public List<FoodItemDto> getAllFoodItems() {
	List<FoodItem> fooditems=fooditemRepo.findAll();
	List<FoodItemDto> response= new ArrayList<>();
	for(FoodItem item :fooditems) {
		FoodItemDto dto= new FoodItemDto();
		dto.setFoodname(item.getFoodname());
		dto.setFoodtype(item.getFoodtype());
		dto.setDescription(item.getDescription());
		dto.setCuisine(item.getCuisine());
		dto.setPrice(item.getPrice());
		dto.setAvailable(item.isAvailable());
		
		if(item.getRestaurant()!=null) {
			dto.setRestaurantId(item.getRestaurant().getId().intValue());
		}
		response.add(dto);
	}
		return response;
	}

	


}
