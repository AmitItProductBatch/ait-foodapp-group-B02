package com.ait.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    Optional<Feedback> findByUser_IdAndRestaurant_IdAndFoodItem_Foodid(
            int userId,
            Long restaurantId,
            int foodItemId
    );

    Optional<Feedback> findByUser_IdAndRestaurant_IdAndFoodItemIsNull(
            int userId,
            Long restaurantId
    );

    List<Feedback> findByRestaurant_Id(Long restaurantId);
}