package com.ait.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.entity.Feedback;
import com.ait.app.service.FeedbackService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

	@Autowired
	FeedbackService feedbackService;

	@PostMapping
	public ResponseEntity<Feedback> createFeedback(@RequestBody Feedback feedback) {

		return new ResponseEntity<>(feedbackService.createFeedback(feedback), HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Feedback> getFeedbackById(@PathVariable int id) {

		return ResponseEntity.ok(feedbackService.getFeedbackById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteFeedback(@PathVariable int id) {

		feedbackService.deleteFeedback(id);

		return ResponseEntity.noContent().build();
	}
}
