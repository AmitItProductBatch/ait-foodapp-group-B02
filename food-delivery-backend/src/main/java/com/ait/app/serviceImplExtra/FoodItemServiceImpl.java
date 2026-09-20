package com.ait.app.serviceImplExtra;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.FoodItemDto;
import com.ait.app.entity.FoodItem;
import com.ait.app.entity.Restaurant;
import com.ait.app.exception.FooditemException;
import com.ait.app.repository.FooditemRepo;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.service.Fooditemservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;



@Service
public class FoodItemServiceImpl implements Fooditemservice{

	private static final Logger log = LoggerFactory.getLogger(FoodItemServiceImpl.class);
	
	@Autowired
	FooditemRepo fooditemRepo;
	
	@Autowired
	RestaurantRepository repository;

	@Override
	public FoodItem addfooditem(FoodItemDto dto) {

		log.info("Starting to add food item");
	
		if(dto==null) {
			log.warn("Food item details are empty");
			throw new FooditemException("fooditem not found", HttpStatus.BAD_REQUEST);
			    }
		
		
		if(dto.getFoodname()==null || dto.getFoodname().trim().isEmpty()) {
			log.warn("Food name is missing");
			throw new FooditemException("Food name cannot be empty",
                    HttpStatus.BAD_REQUEST);
			
		}
		
		
		if(dto.getFoodtype()==null || dto.getFoodtype().trim().isEmpty()) {
			log.warn("Food type is missing");
			
			throw new FooditemException( "Food type must be VEG or NON_VEG",
                    HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getDescription()==null || dto.getDescription().trim().isEmpty()) {
			log.warn("Description is missing");
			throw new FooditemException("Description can not be empty", HttpStatus.BAD_REQUEST);
		}
		

		if(dto.getCuisine()==null || dto.getCuisine().trim().isEmpty()) {
			log.warn("Cuisine is missing");
			throw new FooditemException("cuisine can not be empty", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getRestaurantId()<=0) {
			log.warn("Invalid restaurant id: {}");
			throw new FooditemException("Restaurant id must be greater than 0", HttpStatus.BAD_REQUEST);
		}
		
		Restaurant restaurant = repository.findById((long) dto.getRestaurantId()).orElse(null);

		if (restaurant == null) {
			log.warn("Restaurant not found with id: {}");
		    throw new FooditemException("Restaurant not found with id: " + dto.getRestaurantId(), HttpStatus.NOT_FOUND);
		}
    		
		
		Optional<FoodItem> existingfood=fooditemRepo.findByFoodnameIgnoreCaseAndRestaurantId(dto.getFoodname().trim(),dto.getRestaurantId());
		if(existingfood.isPresent()) {
			log.warn("Food '{}' already exists in restaurant id: {}");
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
	 FoodItem saved=   	fooditemRepo.save(item);
	         log.info("Food item added successfully with id: {}");
			return saved;
	}
    
	@Cacheable(value = "foodItems", key = "#id")
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
		log.info("Deleting food item with id: {}");
		if(id<=0) {
			log.warn("Invalid food item id: {}");
			throw new FooditemException("food item must be greater than 0", HttpStatus.BAD_REQUEST);
		}
	
		if(!fooditemRepo.existsById(id)) {
			log.warn("Food item not found with id: {}");
			throw new FooditemException("food item not found with id:"+id, HttpStatus.NOT_FOUND);
		}
		
		fooditemRepo.deleteById(id);
		log.info("Food item deleted successfully with id: {}");
	}
	
	



	@Override
	public FoodItemDto updateFoodItem(int id, FoodItemDto dto) {
		log.info("Updating food item with id: {}");
	
		if(id<=0) {
			log.warn("Invalid food item id: {}");
			throw new FooditemException("food item ID must be greater than 0", HttpStatus.BAD_REQUEST);
		}
	
		if(dto==null) {
			log.warn("Food item details are empty");
			throw new FooditemException(  "Food item details cannot be null", HttpStatus.BAD_REQUEST);
		}
		
		FoodItem item = fooditemRepo.findById(id).orElse(null);
		if(item==null) {
			log.warn("Food item not found with id: {}");
			throw new FooditemException("Food item not found with id: " + id, HttpStatus.NOT_FOUND);
		}
	
        if (dto.getFoodname() == null || dto.getFoodname().trim().isEmpty()) {
        	log.warn("Food name is missing for food item id: {}");
            throw new FooditemException(  "Food name cannot be empty", HttpStatus.BAD_REQUEST);
        }
 
        if (dto.getFoodtype() == null ||dto.getFoodtype().isEmpty()) {
			log.warn("Food type is missing for food item id: {}");
            throw new FooditemException( "Food type cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (!dto.getFoodtype().equalsIgnoreCase("VEG") && !dto.getFoodtype().equalsIgnoreCase("NON_VEG")) {

			log.warn("Invalid food type '{}' for food item id: {}");

            throw new FooditemException( "Food type must be VEG or NON_VEG", HttpStatus.BAD_REQUEST);
        }
     
        if(dto.getDescription()==null || dto.getDescription().isEmpty()) {
    		log.warn("Description is missing for food item id: {}");
        	throw new FooditemException("Description can not be empty", HttpStatus.BAD_REQUEST);
        }
     
        if(dto.getCuisine()==null || dto.getCuisine().isEmpty()) {
        	log.warn("Cuisine is missing for food item id: {}");
        	throw new FooditemException("cuisine can not be empty", HttpStatus.BAD_REQUEST);
        }
     
        if(dto.getPrice()<=0) {
			log.warn("Invalid price for food item id: {}");
        	throw new FooditemException("price must be greater than 0", HttpStatus.BAD_REQUEST);
        }
       
        if(dto.getRestaurantId()<=0) {
        	log.warn("Invalid restaurant id: {}");
        	throw new FooditemException("Restaurant ID must be greater than 0", HttpStatus.BAD_REQUEST);
        }
     
        Restaurant rest = repository.findById((long)dto.getRestaurantId()).orElse(null);
        if(rest==null) {
        	log.warn("Restaurant not found with id: {}");
        	throw new FooditemException("Restaurant not found with id: " + dto.getRestaurantId(), HttpStatus.NOT_FOUND);
        }
        
        Optional<FoodItem> existingfood=fooditemRepo.findByFoodnameIgnoreCaseAndRestaurantId(dto.getFoodname(), dto.getRestaurantId());
		if(existingfood.isPresent()&&existingfood.get().getFoodid()!=id) {
			log.warn("Food '{}' already exists in restaurant id: {}");
			throw new FooditemException("food already exists in the restaurant", HttpStatus.CONFLICT);
		}
		
		item.setFoodname(dto.getFoodname());
		item.setFoodtype(dto.getFoodtype());
		item.setDescription(dto.getDescription());
		item.setCuisine(dto.getCuisine());
		item.setPrice(dto.getPrice());
		item.setAvailable(dto.isAvailable());
		item.setRestaurant(rest);
		
		FoodItem updateditem= fooditemRepo.save(item);

		FoodItemDto response= new FoodItemDto();
		response.setFoodid(updateditem.getFoodid());
		response.setFoodname(updateditem.getFoodname());
		response.setFoodtype(updateditem.getFoodtype());
		response.setDescription(updateditem.getDescription());
		response.setCuisine(updateditem.getCuisine());
		response.setPrice(updateditem.getPrice());
		response.setAvailable(updateditem.isAvailable());
		response.setRestaurantId(updateditem.getRestaurant().getId().intValue());
		log.info("Food item updated successfully with id: {}");
        return response;
	}

	@Override
	public List<FoodItemDto> getAllFoodItems() {
		log.info("Getting all food items");
	List<FoodItem> fooditems=fooditemRepo.findAll();
	List<FoodItemDto> response= new ArrayList<>();
	for(FoodItem item :fooditems) {
		FoodItemDto dto= new FoodItemDto();
		dto.setFoodid(item.getFoodid());
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
	log.info("Successfully fetched {} food items");
		return response;
	}

	


}
