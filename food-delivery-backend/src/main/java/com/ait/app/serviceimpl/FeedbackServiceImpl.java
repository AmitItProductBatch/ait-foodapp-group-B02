package com.ait.app.serviceimpl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.entity.Feedback;
import com.ait.app.repository.FeedbackRepository;
import com.ait.app.service.FeedbackService;

@Service
public class FeedbackServiceImpl implements FeedbackService {

	@Autowired
	FeedbackRepository feedbackRepository;

	@Override
	public Feedback createFeedback(Feedback feedback) {

		return feedbackRepository.save(feedback);
	}

	@Override
	public Feedback getFeedbackById(int id) {

		return feedbackRepository.findById(id).orElseThrow(() -> new RuntimeException("Feedback not found"));
	}

	@Override
	public void deleteFeedback(int id) {

		feedbackRepository.deleteById(id);
	}

}
