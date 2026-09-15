package com.ait.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.DeliveryPricingRule;

public interface DelieveryPricingRuleRepository extends JpaRepository<DeliveryPricingRule, Integer>{
	Optional<DeliveryPricingRule> findByActiveTrue();

}
