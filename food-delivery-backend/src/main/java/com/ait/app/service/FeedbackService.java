package com.ait.app.service;

import com.ait.app.model.Feedback;

public interface FeedbackService {

	Feedback createFeedback(Feedback feedback);

	Feedback getFeedbackById(int id);

	void deleteFeedback(int id);
}
