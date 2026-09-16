package com.ait.app.serviceImplExtra;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.FeedbackDto;
import com.ait.app.entity.Feedback;
import com.ait.app.entity.FoodItem;
import com.ait.app.entity.Restaurant;
import com.ait.app.entity.User;
import com.ait.app.exception.FeedbackException;
import com.ait.app.exception.FooditemException;
import com.ait.app.repository.FeedbackRepository;
import com.ait.app.repository.FooditemRepo;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.FeedbackService;

@Service
public class FeedbackServiceImpl implements FeedbackService {

	@Autowired
	FeedbackRepository feedbackRepository;

	@Autowired
	RestaurantRepository restaurantRepository;

	@Autowired
	FooditemRepo fooditemRepo;

	@Autowired
	UserRepository userRepository;

	@Override
	public Feedback createFeedback(FeedbackDto feedbackDto) {

		Optional<User> uo = userRepository.findById(feedbackDto.getUserId());

		if (!uo.isPresent()) {
			throw new FeedbackException("User Not Found", HttpStatus.NOT_FOUND);

		}

		User u = uo.get();

		Optional<Restaurant> ro = restaurantRepository.findById(feedbackDto.getRestaurantId());

		if (!ro.isPresent()) {

			throw new FeedbackException("Restaurant Not Found ", HttpStatus.NOT_FOUND);

		}

		Restaurant restaurant = ro.get();

		FoodItem foodItem = null;

		if (feedbackDto.getFoodItemId() != null) {

			Optional<FoodItem> foodOp = fooditemRepo.findById(feedbackDto.getFoodItemId());

			if (!foodOp.isPresent()) {
				throw new FeedbackException("FoodItem Not Found", HttpStatus.NOT_FOUND);

			}

			foodItem = foodOp.get();
		}

		if (feedbackDto.getRating() < 1 || feedbackDto.getRating() > 5) {
			throw new FeedbackException("Rating must be between 1 and 5", HttpStatus.BAD_REQUEST);
		}

		if (foodItem == null) {
			Optional<Feedback> existFeedback = feedbackRepository
					.findByUser_IdAndRestaurant_IdAndFoodItemIsNull(u.getId(), restaurant.getId());

			if (existFeedback.isPresent()) {
				throw new FeedbackException("You have already rated this restaurant", HttpStatus.CONFLICT);

			}

		} else {

			// Duplicate food rating
			Optional<Feedback> existFeedback = feedbackRepository.findByUser_IdAndRestaurant_IdAndFoodItem_Foodid(
					u.getId(), restaurant.getId(), foodItem.getFoodid());

			if (existFeedback.isPresent()) {
				throw new FeedbackException("You have already rated this food item", HttpStatus.CONFLICT);
			}
		}

		Feedback feedback = new Feedback();

		feedback.setUser(u);
		feedback.setRestaurant(restaurant);
		feedback.setFoodItem(foodItem);

		feedback.setRating(feedbackDto.getRating());
		feedback.setComment(feedbackDto.getComment());

		LocalDateTime now = LocalDateTime.now();

		feedback.setCreatedAt(now);
		feedback.setUpdatedAt(now);

		return feedbackRepository.save(feedback);

	}

	@Override
	public List<Feedback> getAllFeedback() {

		return feedbackRepository.findAll();
	}

	@Override
	public List<Feedback> getRestaurantFeedback(Long restaurantId) {

		Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);

		if (!restaurantOptional.isPresent()) {
			throw new FeedbackException("Restaurant Not Found", HttpStatus.NOT_FOUND);

		}

		return feedbackRepository.findByRestaurant_Id(restaurantId);
	}

	@Override
	public Feedback getFeedbackById(int id) {

		Optional<Feedback> o = feedbackRepository.findById(id);
		if (!o.isPresent()) {
			throw new FeedbackException("FeedBack Not Found ", HttpStatus.NOT_FOUND);

		}

		return o.get();
	}

	@Override
	public void deleteFeedback(int id) {

		if (!feedbackRepository.existsById(id)) {

			throw new FeedbackException("Feedback not found", HttpStatus.NOT_FOUND);
		}

		feedbackRepository.deleteById(id);
	}

	@Override
	public Feedback updateFeedback(int feedbackId, FeedbackDto feedbackDto) {

		Optional<Feedback> optional = feedbackRepository.findById(feedbackId);

		if (!optional.isPresent()) {
			throw new FeedbackException("Feedback Not Found", HttpStatus.NOT_FOUND);
		}

		Feedback feedback = optional.get();

		// Check original author
		if (feedback.getUser().getId() != feedbackDto.getUserId()) {

			throw new FeedbackException("You are not allowed to edit this feedback", HttpStatus.FORBIDDEN);
		}

	
		if (feedback.getCreatedAt().plusDays(30).isBefore(LocalDateTime.now())) {

			throw new FeedbackException("Feedback can only be edited within 30 days", HttpStatus.FORBIDDEN);
		}


		if (feedbackDto.getRating() < 1 || feedbackDto.getRating() > 5) {

			throw new FeedbackException("Rating must be between 1 and 5", HttpStatus.BAD_REQUEST);
		}

		
		feedback.setRating(feedbackDto.getRating());
		feedback.setComment(feedbackDto.getComment());

		feedback.setUpdatedAt(LocalDateTime.now());

		return feedbackRepository.save(feedback);
	}

}
