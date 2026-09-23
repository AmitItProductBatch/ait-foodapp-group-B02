package com.ait.app.dto;

import lombok.Data;

@Data
public class DeliveryPartnerRequestDto {

    private String name;

    private String phone;

    private String email;

    private String vehicleNumber;

    private String vehicleType;

    private boolean available;
}