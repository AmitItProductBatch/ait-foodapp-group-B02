package com.ait.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

 
import com.ait.app.model.Restaurant;

@Service
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

}
