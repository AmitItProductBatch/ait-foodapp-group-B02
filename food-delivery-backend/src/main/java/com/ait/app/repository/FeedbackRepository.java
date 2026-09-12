package com.ait.app.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.Feedback;



public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    Optional<com.ait.app.entity.Feedback> findByUserIdAndRestaurantIdAndFoodItemId(
            int userId,
            Long restaurantId,
            Integer foodItemId
    );

    Optional<Feedback> findByUserIdAndRestaurantIdAndFoodItemIdIsNull(
            int userId,
            Long restaurantId
    );

    List<Feedback> findByRestaurantId(Long restaurantId);
}