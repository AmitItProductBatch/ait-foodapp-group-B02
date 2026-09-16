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
		// TODO Auto-generated method stub
		if(delieveryPricingRuleDto==null) {
			 throw new DelieveryPricingRuleException("delievery pricing rule must be greater than zero", HttpStatus.BAD_REQUEST);
		}
		if(delieveryPricingRuleDto.getBasefees()<=0) {
			throw new DelieveryPricingRuleException("delievery base fees must be greater than zero", HttpStatus.BAD_REQUEST);
		}
		if(delieveryPricingRuleDto.getPerKmRate()<0) {
			throw new DelieveryPricingRuleException("delievery per km rate must be greater than zero", HttpStatus.BAD_REQUEST);
		}
		if(delieveryPricingRuleDto.getMaxdelieveryradius()<0|| delieveryPricingRuleDto.getMaxdelieveryradius()!=10){
			throw new DelieveryPricingRuleException("delievery raduis must be greater than zero", HttpStatus.BAD_REQUEST);
		}
		if(delieveryPricingRuleDto.getFreeDelievery()<=0) {
			throw new DelieveryPricingRuleException("free delievery can not be zero", HttpStatus.BAD_REQUEST);
		}
		
		Optional<DeliveryPricingRule> existingdelieverypricingrule =delieveryPricingRuleRepository.findByActiveTrue();
		
			if(existingdelieverypricingrule.isPresent()) {
				throw new DelieveryPricingRuleException("DeliveryPricingRule already exists", HttpStatus.BAD_REQUEST);
			}
				DeliveryPricingRule Pricingrules= new DeliveryPricingRule();
				Pricingrules.setBasefees(delieveryPricingRuleDto.getBasefees());
				Pricingrules.setPerKmRate(delieveryPricingRuleDto.getPerKmRate());
				Pricingrules.setMaxdelieveryradius(delieveryPricingRuleDto.getMaxdelieveryradius());
				Pricingrules.setFreeDelievery(delieveryPricingRuleDto.getFreeDelievery());
				Pricingrules.setActive(delieveryPricingRuleDto.isActive());
				Pricingrules.setActive(true);
				
				DeliveryPricingRule saverule=delieveryPricingRuleRepository.save(Pricingrules);
				
				DelieveryPricingRuleDto responseDto=new DelieveryPricingRuleDto();
				responseDto.setBasefees(saverule.getBasefees());
				responseDto.setFreeDelievery(saverule.getFreeDelievery());
				responseDto.setMaxdelieveryradius(saverule.getMaxdelieveryradius());
				responseDto.setPerKmRate(saverule.getPerKmRate());
				responseDto.setActive(saverule.isActive());
				return responseDto;
	}

	@Override
	public DelieveryPricingRuleDto getDeliveryPricingRule(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DelieveryPricingRuleDto> getAllDeliveryPricingRule() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deletebyDeliveryPricingRule(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DelieveryPricingRuleDto updateDeliveryPricingRule(DelieveryPricingRuleDto delieveryPricingRuleDto, int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
