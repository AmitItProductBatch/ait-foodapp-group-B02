package com.ait.app.serviceImplExtra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ait.app.entity.Role;
import com.ait.app.repository.RoleRepository;
import com.ait.app.repository.UserRepository;

@Service
public class UserDetailServiceImpl implements UserDetailsService{

	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository rolerepository;
	@Override
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

		com.ait.app.entity.User u = userRepository.loadByName(name);
		
		  UserDetails ud = User.builder()
	                .username(u.getName())
	                .password(u.getPassword())
	                .roles(u.getRole().getRoleName())
	                .build();
		
		
		return ud;
	}

	
public	String getRole(String name) {
		
	com.ait.app.entity.User users =userRepository.loadByName(name);
	
	
		
	return users.getRole().getRoleName();
	}
}
