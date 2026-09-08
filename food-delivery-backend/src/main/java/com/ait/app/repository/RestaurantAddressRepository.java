package com.ait.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.ait.app.entity.RestaurantAddress;

@Service
public interface RestaurantAddressRepository extends JpaRepository<RestaurantAddress, Long> {

}
