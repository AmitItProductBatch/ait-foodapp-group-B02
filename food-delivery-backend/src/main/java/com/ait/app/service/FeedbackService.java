package com.ait.app.service;

import java.util.List;

import com.ait.app.dto.FeedbackDto;
import com.ait.app.entity.Feedback;

public interface FeedbackService {
	
	Feedback createFeedback(FeedbackDto feedbackDto);

	List<Feedback> getAllFeedback();

	List<Feedback> getRestaurantFeedback(Long restaurantId);

	Feedback getFeedbackById(int id);

	void deleteFeedback(int id);
	
	Feedback updateFeedback(int feedbackId, FeedbackDto feedbackDto);
}
