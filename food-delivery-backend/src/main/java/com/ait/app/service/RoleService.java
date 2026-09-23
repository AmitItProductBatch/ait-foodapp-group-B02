package com.ait.app.service;

import java.util.List;

import com.ait.app.dto.RoleResponse;
import com.ait.app.entity.Role;

public interface RoleService {

	    void saveRole(Role r);

	    RoleResponse getRole(int id);

	    List<RoleResponse> getAllRoles();

	    void deleteRole(int id);
}
