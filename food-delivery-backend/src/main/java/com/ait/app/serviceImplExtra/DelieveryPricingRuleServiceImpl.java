package com.ait.app.serviceImplExtra;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.DelieveryPricingRuleDto;
import com.ait.app.entity.DeliveryPricingRule;
import com.ait.app.exception.DelieveryPricingRuleException;
import com.ait.app.repository.DelieveryPricingRuleRepository;
import com.ait.app.service.DelieveryPricingRule;
@Service
public class DelieveryPricingRuleServiceImpl implements DelieveryPricingRule{
	
	@Autowired
	DelieveryPricingRuleRepository  delieveryPricingRuleRepository;

	@Override
	public DelieveryPricingRuleDto addDeliveryPricingRule(DelieveryPricingRuleDto delieveryPricingRuleDto) {
		if(delieveryPricingRuleDto==null) {
			 throw new DelieveryPricingRuleException("delivery pricing rule must be provided", HttpStatus.BAD_REQUEST);
		}
		if(delieveryPricingRuleDto.getBasefees()<=0) {
			throw new DelieveryPricingRuleException("delivery base fees must be greater than zero", HttpStatus.BAD_REQUEST);
		}
		if(delieveryPricingRuleDto.getPerKmRate()<0) {
			throw new DelieveryPricingRuleException("delivery per km rate must be 0 or greater", HttpStatus.BAD_REQUEST);
		}
		if(delieveryPricingRuleDto.getMaxdelieveryradius()<=0){
			throw new DelieveryPricingRuleException("delivery radius must be greater than zero", HttpStatus.BAD_REQUEST);
		}
		if(delieveryPricingRuleDto.getFreeDelievery()<=0) {
			throw new DelieveryPricingRuleException("free delivery threshold must be greater than zero", HttpStatus.BAD_REQUEST);
		}
		
		Optional<DeliveryPricingRule> existingdelieverypricingrule = delieveryPricingRuleRepository.findByActiveTrue();
		DeliveryPricingRule pricingRules;
		
		if(existingdelieverypricingrule.isPresent()) {
			pricingRules = existingdelieverypricingrule.get();
		} else {
			pricingRules = new DeliveryPricingRule();
		}
		
		pricingRules.setBasefees(delieveryPricingRuleDto.getBasefees());
		pricingRules.setPerKmRate(delieveryPricingRuleDto.getPerKmRate());
		pricingRules.setMaxdelieveryradius(delieveryPricingRuleDto.getMaxdelieveryradius());
		pricingRules.setFreeDelievery(delieveryPricingRuleDto.getFreeDelievery());
		pricingRules.setActive(true);
		
		DeliveryPricingRule saverule = delieveryPricingRuleRepository.save(pricingRules);
		
		DelieveryPricingRuleDto responseDto = new DelieveryPricingRuleDto();
		responseDto.setBasefees(saverule.getBasefees());
		responseDto.setFreeDelievery(saverule.getFreeDelievery());
		responseDto.setMaxdelieveryradius(saverule.getMaxdelieveryradius());
		responseDto.setPerKmRate(saverule.getPerKmRate());
		responseDto.setActive(saverule.isActive());
		return responseDto;
	}

	@Override
	public DelieveryPricingRuleDto getDeliveryPricingRule(int id) {
		Optional<DeliveryPricingRule> rule = delieveryPricingRuleRepository.findById(id);
		if(rule.isEmpty()) {
			throw new DelieveryPricingRuleException("DeliveryPricingRule not found", HttpStatus.NOT_FOUND);
		}
		DeliveryPricingRule saverule = rule.get();
		DelieveryPricingRuleDto responseDto = new DelieveryPricingRuleDto();
		responseDto.setBasefees(saverule.getBasefees());
		responseDto.setFreeDelievery(saverule.getFreeDelievery());
		responseDto.setMaxdelieveryradius(saverule.getMaxdelieveryradius());
		responseDto.setPerKmRate(saverule.getPerKmRate());
		responseDto.setActive(saverule.isActive());
		return responseDto;
	}

	@Override
	public List<DelieveryPricingRuleDto> getAllDeliveryPricingRule() {
		List<DeliveryPricingRule> list = delieveryPricingRuleRepository.findAll();
		java.util.List<DelieveryPricingRuleDto> result = new java.util.ArrayList<>();
		for(DeliveryPricingRule saverule : list) {
			DelieveryPricingRuleDto responseDto = new DelieveryPricingRuleDto();
			responseDto.setBasefees(saverule.getBasefees());
			responseDto.setFreeDelievery(saverule.getFreeDelievery());
			responseDto.setMaxdelieveryradius(saverule.getMaxdelieveryradius());
			responseDto.setPerKmRate(saverule.getPerKmRate());
			responseDto.setActive(saverule.isActive());
			result.add(responseDto);
		}
		return result;
	}

	@Override
	public void deletebyDeliveryPricingRule(int id) {
		delieveryPricingRuleRepository.deleteById(id);
	}

	@Override
	public DelieveryPricingRuleDto updateDeliveryPricingRule(DelieveryPricingRuleDto delieveryPricingRuleDto, int id) {
		return addDeliveryPricingRule(delieveryPricingRuleDto);
	}

}
