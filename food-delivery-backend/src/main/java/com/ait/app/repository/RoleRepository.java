package com.ait.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	
	boolean existsByRoleName(String roleName);
}
