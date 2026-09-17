package com.ait.app.service;

import java.util.List;

import com.ait.app.dto.DelieveryPricingRuleDto;


public interface DelieveryPricingRule {
	
	DelieveryPricingRuleDto addDeliveryPricingRule(DelieveryPricingRuleDto delieveryPricingRuleDto);
	DelieveryPricingRuleDto getDeliveryPricingRule(int id);
	List<DelieveryPricingRuleDto> getAllDeliveryPricingRule();
	void deletebyDeliveryPricingRule(int id);
	DelieveryPricingRuleDto updateDeliveryPricingRule(DelieveryPricingRuleDto delieveryPricingRuleDto, int id);

}
