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
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

	@Autowired
	FeedbackService feedbackService;

	@PostMapping
	public ResponseEntity<String> createFeedback(@RequestBody FeedbackDto feedbackDto) {

	    feedbackService.createFeedback(feedbackDto);

	    return new ResponseEntity<>(
	            "Feedback Created Successfully",
	            HttpStatus.CREATED
	    );
	}
	
	@GetMapping
	public ResponseEntity<List<Feedback>> getAllFeedback(){
		 return ResponseEntity.ok(
	                feedbackService.getAllFeedback()
	        );
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<Feedback> getFeedbackById(@PathVariable int id) {

		return ResponseEntity.ok(feedbackService.getFeedbackById(id));
	}

	
	  @GetMapping("/restaurant/{restaurantId}")
	  public ResponseEntity<List<Feedback>>getRestaurantFeedback(@PathVariable Long restaurantId){
		  return ResponseEntity.ok(feedbackService.getRestaurantFeedback(restaurantId));
	  }
	
	
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteFeedback(@PathVariable int id) {

		feedbackService.deleteFeedback(id);

		return ResponseEntity.ok( "Feedback Deleted Successfully");
	}
}
