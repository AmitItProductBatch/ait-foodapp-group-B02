package com.ait.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.FeedbackDto;
import com.ait.app.entity.Feedback;
import com.ait.app.service.FeedbackService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

	@Autowired
	FeedbackService feedbackService;

	@PostMapping
	public ResponseEntity<String> createFeedback(@RequestBody FeedbackDto feedbackDto) {

		feedbackService.createFeedback(feedbackDto);

		return new ResponseEntity<>("Feedback Created Successfully", HttpStatus.CREATED);

	}

	@GetMapping()
	ResponseEntity<List<Feedback>> getAllFeedback() {

		List<Feedback> feedbacks = feedbackService.getAllFeedback();

		return new ResponseEntity<>(feedbacks, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	ResponseEntity<Feedback> getFeedbackById(@PathVariable int id) {

		Feedback feedback = feedbackService.getFeedbackById(id);

		return new ResponseEntity<>(feedback, HttpStatus.OK);
	}

	@GetMapping("/restaurant/{restaurantId}")
	ResponseEntity<List<Feedback>> getRestaurantFeedback(@PathVariable Long restaurantId) {

		List<Feedback> feedbacks = feedbackService.getRestaurantFeedback(restaurantId);

		return new ResponseEntity<>(feedbacks, HttpStatus.OK);
	}

	@PutMapping("/{id}")
	ResponseEntity<Feedback> updateFeedback(@PathVariable int id, @RequestBody FeedbackDto feedbackDto)

	{

		Feedback updatedFeedback = feedbackService.updateFeedback(id, feedbackDto);

		return new ResponseEntity<>(updatedFeedback, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	ResponseEntity<String> deleteFeedback(@PathVariable int id) {

		feedbackService.deleteFeedback(id);

		return new ResponseEntity<>("Feedback Deleted Successfully", HttpStatus.OK);

	}
}
