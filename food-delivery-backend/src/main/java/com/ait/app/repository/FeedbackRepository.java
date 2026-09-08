package com.ait.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

	
}
