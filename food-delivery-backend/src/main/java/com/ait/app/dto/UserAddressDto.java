package com.ait.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserAddressDto {
 
    private String houseNo;

    private String buildingName;

    private String street;

    private String landmark;

    private String area;
    
    private String city;

    private String state;

    private Long pincode;

    private String addressType;

    private int userId;
}