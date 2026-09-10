package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.UserAddressDto;
import com.ait.app.dto.UserAddressDto1;
import com.ait.app.service.UserAddressService;

@RestController
@RequestMapping("/api/user-address")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @PostMapping
    public ResponseEntity saveAddress(@RequestBody UserAddressDto addressDto) {
        userAddressService.saveAddress(addressDto);
        return new ResponseEntity<>("Address saved successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<UserAddressDto1> getAddressById(@PathVariable int addressId) {
        UserAddressDto1 address = userAddressService.getAddressById(addressId);
        return ResponseEntity.ok(address);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity deleteAddress(@PathVariable int addressId) {
        userAddressService.deleteAddress(addressId);
        return ResponseEntity.ok("Address deleted successfully");
    }
}