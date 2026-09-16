package com.ait.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "user_addresses")
@Data

public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String houseNo; 
    private String buildingName;

    private String street; 
    private String landmark;
    private String area; 
    private String city;

    private String state;

    private Long pincode;

    private String addressType; 
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}