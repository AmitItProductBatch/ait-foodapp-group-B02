package com.ait.app.serviceImplExtra;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.FeedbackDto;
import com.ait.app.entity.Feedback;
import com.ait.app.exception.FeedbackException;
import com.ait.app.exception.FooditemException;
import com.ait.app.repository.FeedbackRepository;
import com.ait.app.repository.FooditemRepo;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.service.FeedbackService;

@Service
public class FeedbackServiceImpl implements FeedbackService {

	@Autowired
	FeedbackRepository feedbackRepository;

	@Autowired
	RestaurantRepository restaurantRepository;

	@Autowired
	FooditemRepo fooditemRepo;

	@Override
	public Feedback createFeedback(FeedbackDto feedbackDto) {

		if (!restaurantRepository.findById(feedbackDto.getRestaurantId()).isPresent()) {

			throw new FeedbackException("Restaurant Not found", HttpStatus.NOT_FOUND);
		}

		if (feedbackDto.getFoodItemId() != null) {

			if (!fooditemRepo.findById(feedbackDto.getFoodItemId()).isPresent()) {

				throw new FeedbackException("FoodItem Not found", HttpStatus.NOT_FOUND);
			}
		}

		if (feedbackDto.getRating() < 1 || feedbackDto.getRating() > 5) {

			throw new FeedbackException("Rating must be between 1 and 5", HttpStatus.BAD_REQUEST);
		}

		if (feedbackDto.getFoodItemId() == null) {

			if (feedbackRepository.findByUserIdAndRestaurantIdAndFoodItemIdIsNull(feedbackDto.getUserId(),
					feedbackDto.getRestaurantId()).isPresent()) {

				throw new FeedbackException("You have already rated this restaurant", HttpStatus.CONFLICT);
			}

		} else {

			if (feedbackRepository.findByUserIdAndRestaurantIdAndFoodItemId(feedbackDto.getUserId(),
					feedbackDto.getRestaurantId(), feedbackDto.getFoodItemId()).isPresent()) {

				throw new FeedbackException("You have already rated this food item", HttpStatus.CONFLICT);
			}
		}

		Feedback feedback = new Feedback();

		feedback.setUserId(feedbackDto.getUserId());
		feedback.setRestaurantId(feedbackDto.getRestaurantId());
		feedback.setFoodItemId(feedbackDto.getFoodItemId());
		feedback.setRating(feedbackDto.getRating());
		feedback.setComment(feedbackDto.getComment());

		feedback.setCreatedAt(LocalDateTime.now());
		feedback.setUpdatedAt(LocalDateTime.now());

		return feedbackRepository.save(feedback);
	}

	@Override
	public List<Feedback> getAllFeedback() {

		return feedbackRepository.findAll();
	}

	@Override
	public List<Feedback> getRestaurantFeedback(Long restaurantId) {

		if (!restaurantRepository.findById(restaurantId).isPresent()) {

			throw new FeedbackException("Restaurant Not Found", HttpStatus.NOT_FOUND);
		}

		return feedbackRepository.findByRestaurantId(restaurantId);
	}

	@Override
	public Feedback getFeedbackById(int id) {

		return feedbackRepository.findById(id)
				.orElseThrow(() -> new FeedbackException("Feedback not found", HttpStatus.NOT_FOUND));
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
	
		Feedback feedback = feedbackRepository.findById(feedbackId).get();
		
		if(feedback== null) {
			throw new FooditemException("Feedback Not Found", HttpStatus.NOT_FOUND);
		}
		
		if(feedback.getUserId() !=feedbackDto.getUserId() ) {
			throw new FeedbackException("You are not allowe to edit this feedback ", HttpStatus.FORBIDDEN);
			
		}
		
		
		if(feedback.getCreatedAt().plusDays(30).isBefore(LocalDateTime.now())) {
			
			throw new FeedbackException("Feedback can Only be edited within 30 days ", HttpStatus.FORBIDDEN);
		}
		
		if(feedback.getRating()<1|| feedback.getRating()>5) {
			
			throw new FeedbackException("Rating must between 1 to 5", HttpStatus.BAD_REQUEST);
			
			
		}
		
		
		feedback.setComment(feedbackDto.getComment());
		feedback.setRating(feedbackDto.getRating());
		feedback.setUpdatedAt(LocalDateTime.now());
		
		
		
		return feedbackRepository.save(feedback);
	}
}