package com.ait.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.UserAddress;

public interface UserAddressRepository extends JpaRepository<UserAddress,Integer> {

}
