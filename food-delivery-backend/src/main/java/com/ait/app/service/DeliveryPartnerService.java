package com.ait.app.service;

import java.util.List;

import com.ait.app.dto.DeliveryPartnerRequestDto;
import com.ait.app.entity.DeliveryPartner;

public interface DeliveryPartnerService {

    DeliveryPartner addDeliveryPartner(
            DeliveryPartnerRequestDto dto);

    DeliveryPartner getDeliveryPartner(Long id);

    List<DeliveryPartner> getAllDeliveryPartners();

    DeliveryPartner updateDeliveryPartner(
            Long id,
            DeliveryPartnerRequestDto dto);

    void deleteDeliveryPartner(Long id);
}