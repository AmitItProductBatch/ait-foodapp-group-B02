package com.ait.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.DeliveryPartner;

public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {
	
	 Optional<DeliveryPartner> findFirstByAvailableTrue();

}
