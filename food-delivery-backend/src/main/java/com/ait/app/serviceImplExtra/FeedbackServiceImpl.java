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
import com.ait.app.repository.FeedbackRepository;
import com.ait.app.repository.FooditemRepo;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.FeedbackService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

        log.info("Creating feedback for userId: {} and restaurantId: {}", feedbackDto.getUserId(),feedbackDto.getRestaurantId());


        Optional<User> uo = userRepository.findById(feedbackDto.getUserId());

        if (!uo.isPresent()) 
        {
            log.warn("User not found with id: {}", feedbackDto.getUserId());

            throw new FeedbackException( "User Not Found",HttpStatus.NOT_FOUND);
                   
        }

        User u = uo.get();

        Optional<Restaurant> ro = restaurantRepository.findById(feedbackDto.getRestaurantId());
               

        if (!ro.isPresent())
        {
            log.warn("Restaurant not found with id: {}",feedbackDto.getRestaurantId());
                    
            throw new FeedbackException("Restaurant Not Found", HttpStatus.NOT_FOUND);
                                       
        }

        Restaurant restaurant = ro.get();

        FoodItem foodItem = null;

        if (feedbackDto.getFoodItemId() != null) {

            log.debug("Fetching food item with id: {}", feedbackDto.getFoodItemId());
                   

            Optional<FoodItem> foodOp = fooditemRepo.findById(feedbackDto.getFoodItemId());
                  

            if (!foodOp.isPresent()) 
            {
                log.warn("Food item not found with id: {}", feedbackDto.getFoodItemId());
                       
                throw new FeedbackException( "FoodItem Not Found", HttpStatus.NOT_FOUND);
                                         
            }

            foodItem = foodOp.get();
        }

        if (feedbackDto.getRating() < 1 || feedbackDto.getRating() > 5)
          
        {

            log.warn("Invalid rating: {} for userId: {}", feedbackDto.getRating(), feedbackDto.getUserId());
                   
                   
            throw new FeedbackException("Rating must be between 1 and 5", HttpStatus.BAD_REQUEST);
                    
                   
        }

        if (foodItem == null) {

            log.debug("Checking duplicate restaurant feedback for userId: {} and restaurantId: {}", u.getId(), restaurant.getId());
                   
                    

            Optional<Feedback> existFeedback = feedbackRepository.findByUser_IdAndRestaurant_IdAndFoodItemIsNull
            		                           ( u.getId(), restaurant.getId());
                   
                           
                 
            if (existFeedback.isPresent()) 
            {

                log.warn("User {} has already rated restaurant {}",u.getId(),restaurant.getId());

                       
                throw new FeedbackException( "You have already rated this restaurant", HttpStatus.CONFLICT);
                       
                       
            }

        } else {

            log.debug("Checking duplicate food feedback for userId: {}, restaurantId: {} and foodItemId: {}",
                    u.getId(),
                    restaurant.getId(),
                    foodItem.getFoodid());

            Optional<Feedback> existFeedback = feedbackRepository.findByUser_IdAndRestaurant_IdAndFoodItem_Foodid(u.getId(),restaurant.getId(),foodItem.getFoodid());
            
                           
            if (existFeedback.isPresent()) {

                log.warn("User {} has already rated food item {}", u.getId(),foodItem.getFoodid());
                       
                       
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

        Feedback savedFeedback = feedbackRepository.save(feedback);

        log.info("Feedback created successfully with id: {}",
                savedFeedback.getId());

        return savedFeedback;
    }

    @Override
    public List<Feedback> getAllFeedback() {

        log.debug("Fetching all feedbacks");

        long startTime = System.currentTimeMillis();

        List<Feedback> feedbackList = feedbackRepository.findAll();
               

        long executionTime =System.currentTimeMillis() - startTime;
                

        log.info("Feedback search completed in {} ms", executionTime);
               

        if (feedbackList.isEmpty()) {

            log.warn("No feedbacks found");

            throw new FeedbackException("No feedbacks found", HttpStatus.NOT_FOUND);
                    
                   
        }

        log.info("{} feedbacks found",feedbackList.size());
                

        return feedbackList;
    }

    @Override
    public List<Feedback> getRestaurantFeedback(Long restaurantId) {

        log.debug("Fetching feedbacks for restaurantId: {}",restaurantId);
                

        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);
               

        if (!restaurantOptional.isPresent()) {

            log.warn("Restaurant not found with id: {}", restaurantId);
                   

            throw new FeedbackException( "Restaurant Not Found", HttpStatus.NOT_FOUND);
                   
                   
        }

        List<Feedback> feedbackList =feedbackRepository.findByRestaurant_Id(restaurantId);
                

        log.info("{} feedbacks found for restaurantId: {}",feedbackList.size(),restaurantId);
                
                

        return feedbackList;
    }

    @Override
    public Feedback getFeedbackById(int id) {

        log.debug("Fetching feedback with id: {}", id);
               

        Optional<Feedback> o = feedbackRepository.findById(id);
               

        if (!o.isPresent()) {

            log.warn("Feedback not found with id: {}", id);
                   

            throw new FeedbackException( "FeedBack Not Found",HttpStatus.NOT_FOUND);
            
                    
        }

        log.info("Feedback found with id: {}", id);
               

        return o.get();
    }

    @Override
    public void deleteFeedback(int id) {

        log.info("Deleting feedback with id: {}",id);
                

        if (!feedbackRepository.existsById(id)) {

            log.warn("Feedback not found with id: {}", id);
                   

            throw new FeedbackException("Feedback not found", HttpStatus.NOT_FOUND);
                    
                   
        }

        feedbackRepository.deleteById(id);

        log.info("Feedback deleted successfully with id: {}",id);
                
    }

    @Override
    public Feedback updateFeedback(int feedbackId,FeedbackDto feedbackDto)
            
             {

        log.info("Updating feedback with id: {}",feedbackId);
                

        Optional<Feedback> optional = feedbackRepository.findById(feedbackId);
               

        if (!optional.isPresent()) {

            log.warn("Feedback not found with id: {}", feedbackId);
                   

            throw new FeedbackException("Feedback Not Found",HttpStatus.NOT_FOUND);

                                        
        }

        Feedback feedback = optional.get();

        if (feedback.getUser().getId() != feedbackDto.getUserId()) {

            log.warn("User {} is not allowed to edit feedback with id: {}", feedbackDto.getUserId(), feedbackId);
                   
                   

            throw new FeedbackException("You are not allowed to edit this feedback", HttpStatus.FORBIDDEN);

                   
        }

        if (feedback.getCreatedAt().plusDays(30).isBefore(LocalDateTime.now()))
                
                 {

            log.warn("Feedback with id: {} cannot be edited after 30 days",  feedbackId);
                  

            throw new FeedbackException( "Feedback can only be edited within 30 days", HttpStatus.FORBIDDEN);
                   
                   
        }

        if (feedbackDto.getRating() < 1 || feedbackDto.getRating() > 5)
                 {

            log.warn("Invalid rating: {} for feedback id: {}", feedbackDto.getRating(), feedbackId);
                   
                   

            throw new FeedbackException( "Rating must be between 1 and 5",HttpStatus.BAD_REQUEST);
                   
                    
        }

        feedback.setRating(feedbackDto.getRating());
        feedback.setComment(feedbackDto.getComment());
        feedback.setUpdatedAt(LocalDateTime.now());

        Feedback updatedFeedback =feedbackRepository.save(feedback);
                

        log.info("Feedback updated successfully with id: {}", feedbackId);
               

        return updatedFeedback;
    }
}