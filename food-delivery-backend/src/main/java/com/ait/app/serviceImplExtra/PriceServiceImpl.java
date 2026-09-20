package com.ait.app.serviceImplExtra;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.PriceResponse;
import com.ait.app.entity.FoodItem;
import com.ait.app.exception.FooditemException;
import com.ait.app.repository.FooditemRepo;
import com.ait.app.service.PriceService;

@Service
public class PriceServiceImpl implements PriceService {

	private static final Logger log = LoggerFactory.getLogger(PriceServiceImpl.class);

	@Autowired
	FooditemRepo fooditemRepo;

	@Override
	public PriceResponse getPrice(int itemId) {

		if (itemId <= 0) {

			log.warn("Invalid food item id: {}", itemId);
			throw new FooditemException("Invalid food item id", HttpStatus.BAD_REQUEST);
		}

		Optional<FoodItem> o = fooditemRepo.findById(itemId);
		if (o.isEmpty()) {
			
			log.warn("Food item not found with id: {}", itemId);
			throw new FooditemException("Food item not found", HttpStatus.NOT_FOUND);
		}

		FoodItem foodItem = o.get();
		
		PriceResponse pr = new PriceResponse();
		
		pr.setFoodName(foodItem.getFoodname());
		pr.setPrice(foodItem.getPrice());
		
        log.info("Price fetched successfully for food item id: {}", itemId);

		return pr;
	}

}
