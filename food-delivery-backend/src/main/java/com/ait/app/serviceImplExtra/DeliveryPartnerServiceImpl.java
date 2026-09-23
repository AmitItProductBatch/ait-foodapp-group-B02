package com.ait.app.serviceImplExtra;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.DeliveryPartnerRequestDto;
import com.ait.app.entity.DeliveryPartner;
import com.ait.app.exception.DeliveryPartnerException;
import com.ait.app.repository.DeliveryPartnerRepository;
import com.ait.app.service.DeliveryPartnerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DeliveryPartnerServiceImpl implements DeliveryPartnerService {

    @Autowired
    DeliveryPartnerRepository deliveryPartnerRepository;

    @Override
    public DeliveryPartner addDeliveryPartner(
            DeliveryPartnerRequestDto dto) {

        log.info("Adding new delivery partner with name: {}", dto.getName());

        DeliveryPartner deliveryPartner = new DeliveryPartner();

        deliveryPartner.setName(dto.getName());
        deliveryPartner.setPhone(dto.getPhone());
        deliveryPartner.setEmail(dto.getEmail());
        deliveryPartner.setVehicleNumber(dto.getVehicleNumber());
        deliveryPartner.setVehicleType(dto.getVehicleType());
        deliveryPartner.setAvailable(dto.isAvailable());

        DeliveryPartner savedPartner =
                deliveryPartnerRepository.save(deliveryPartner);

        log.info("Delivery partner added successfully with id: {}",
                savedPartner.getId());

        return savedPartner;
    }

    @Override
    public DeliveryPartner getDeliveryPartner(Long id) {

        log.info("Fetching delivery partner with id: {}", id);

        DeliveryPartner deliveryPartner =
                deliveryPartnerRepository.findById(id).orElse(null);

        if (deliveryPartner == null) {

            log.warn("Delivery partner not found with id: {}", id);

            throw new DeliveryPartnerException(
                    "Delivery partner not found",
                    HttpStatus.NOT_FOUND);
        }

        log.info("Delivery partner found with id: {}", id);

        return deliveryPartner;
    }

    @Override
    public List<DeliveryPartner> getAllDeliveryPartners() {

        log.info("Fetching all delivery partners");

        List<DeliveryPartner> deliveryPartners =
                deliveryPartnerRepository.findAll();

        log.info("Total delivery partners found: {}",
                deliveryPartners.size());

        return deliveryPartners;
    }

    @Override
    public DeliveryPartner updateDeliveryPartner(
            Long id,
            DeliveryPartnerRequestDto dto) {

        log.info("Updating delivery partner with id: {}", id);

        DeliveryPartner deliveryPartner =
                deliveryPartnerRepository.findById(id).orElse(null);

        if (deliveryPartner == null) {

            log.warn("Cannot update. Delivery partner not found with id: {}",
                    id);

            throw new DeliveryPartnerException(
                    "Delivery partner not found",
                    HttpStatus.NOT_FOUND);
        }

        deliveryPartner.setName(dto.getName());
        deliveryPartner.setPhone(dto.getPhone());
        deliveryPartner.setEmail(dto.getEmail());
        deliveryPartner.setVehicleNumber(dto.getVehicleNumber());
        deliveryPartner.setVehicleType(dto.getVehicleType());
        deliveryPartner.setAvailable(dto.isAvailable());

        DeliveryPartner updatedPartner =
                deliveryPartnerRepository.save(deliveryPartner);

        log.info("Delivery partner updated successfully with id: {}",
                id);

        return updatedPartner;
    }

    @Override
    public void deleteDeliveryPartner(Long id) {

        log.info("Deleting delivery partner with id: {}", id);

        if (!deliveryPartnerRepository.existsById(id)) {

            log.warn("Cannot delete. Delivery partner not found with id: {}",
                    id);

            throw new DeliveryPartnerException(
                    "Delivery partner not found",
                    HttpStatus.NOT_FOUND);
        }

        deliveryPartnerRepository.deleteById(id);

        log.info("Delivery partner deleted successfully with id: {}", id);
    }
}