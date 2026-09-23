package com.ait.app.serviceImplExtra;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.RoleResponse;
import com.ait.app.entity.Role;
import com.ait.app.exception.RoleException;

import com.ait.app.repository.RoleRepository;
import com.ait.app.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

	private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

	@Autowired
	RoleRepository roleRepository;

	@Override
	public void saveRole(Role r) {

		log.info("Starting role registration");

		if (r == null) {

			log.warn("Role details are empty");

			throw new RoleException("Role details cannot be empty", HttpStatus.BAD_REQUEST);
		}

		if (r.getRoleName() == null || r.getRoleName().isEmpty()) {

			log.warn("Role name is missing");

			throw new RoleException("Role name cannot be empty", HttpStatus.BAD_REQUEST);
		}

		r.setRoleName(r.getRoleName().toUpperCase());

		if (roleRepository.existsByRoleName(r.getRoleName())) {

			log.warn("Role already exists: {}", r.getRoleName());

			throw new RoleException("Role is already registered", HttpStatus.CONFLICT);
		}

		roleRepository.save(r);

		log.info("Role registered successfully: {}", r.getRoleName());
	}

	@Override
	public RoleResponse getRole(int id) {

		log.info("Getting role with id: {}", id);

		if (id <= 0) {

			log.warn("Invalid role id: {}", id);
			throw new RoleException("Role id must be greater than 0", HttpStatus.BAD_REQUEST);
		}

		Optional<Role> o = roleRepository.findById(id);

		if (o.isEmpty()) {

			log.warn("Role not found with id: {}", id);
			throw new RoleException("Role not found with id: " + id, HttpStatus.NOT_FOUND);
		}

		Role role = o.get();
		RoleResponse r = new RoleResponse();

		r.setRoleName(role.getRoleName());
		log.info("Role fetched successfully with id: {}", id);
		return r;
	}

	@Override
	public List<RoleResponse> getAllRoles() {

		log.info("Getting all roles");
		List<Role> roles = roleRepository.findAll();

		if (roles.isEmpty()) {

			log.warn("No roles found");
			throw new RoleException("No roles found", HttpStatus.NOT_FOUND);
		}

		List<RoleResponse> l = new ArrayList<>();

		for (Role role : roles) {

			RoleResponse response = new RoleResponse();

			response.setRoleName(role.getRoleName());

			l.add(response);
		}

		log.info("Successfully fetched {} roles");

		return l;
	}

	@Override
	public void deleteRole(int id) {

		log.info("Deleting role with id: {}", id);

		if (id <= 0) {

			log.warn("Invalid role id: {}", id);
			throw new RoleException("Role id must be greater than 0", HttpStatus.BAD_REQUEST);
		}

		if (!roleRepository.existsById(id)) {

			log.warn("Role not found with id: {}", id);
			throw new RoleException("Role not found with id: " + id, HttpStatus.NOT_FOUND);
		}

		roleRepository.deleteById(id);
		log.info("Role deleted successfully with id: {}", id);
	}

}
