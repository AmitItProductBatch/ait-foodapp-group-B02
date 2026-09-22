package com.ait.app.controller;

import java.util.List;

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

import com.ait.app.dto.RoleResponse;
import com.ait.app.entity.Role;
import com.ait.app.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

	@Autowired
	RoleService roleService;

	@PostMapping
	public ResponseEntity saveRole(@RequestBody Role role) {

		roleService.saveRole(role);

		return new ResponseEntity("Role successfully added", HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity getRole(@PathVariable int id) {

		RoleResponse role = roleService.getRole(id);

		return new ResponseEntity(role, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity getAllRoles() {

		List<RoleResponse> roles = roleService.getAllRoles();

		return new ResponseEntity(roles, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteRole(@PathVariable int id) {

		roleService.deleteRole(id);

		return new ResponseEntity("Role deleted successfully", HttpStatus.OK);
	}
}
