package com.ait.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

	boolean existsByRole(String Role);
}
