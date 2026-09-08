package com.ait.app.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "user_addresses")
@Data

public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    // --- Specific Address Breakdown ---
    
  
    private String houseNo; // e.g., "Flat 402", "House 12-A"

    private String buildingName; // e.g., "Greenwood Apartments"


    private String street; // e.g., "1st Main Road, Sector 4"

    private String landmark; // e.g., "Behind City Hospital"

    private String area; // e.g., "Koramangala"
    
    private String city;

    private String state;

    private Long pincode;

    // --- Food Delivery Specific Meta-data ---

    private String addressType; // HOME, WORK, OTHER

    //private User user;
}