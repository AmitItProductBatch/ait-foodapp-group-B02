package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.config.JwtUtil;
import com.ait.app.dto.LoginDto;
import com.ait.app.serviceImplExtra.UserDetailServiceImpl;

@RestController
public class AuthController {

	
	
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	
		@Autowired
		JwtUtil jwtUtil;
		
		
		@Autowired
		UserDetailServiceImpl userDetailServiceImpl;
			
	
	@PostMapping("/auth")
	String authController( @RequestBody LoginDto loginDto) {
		
		UsernamePasswordAuthenticationToken authenticates = 
				new UsernamePasswordAuthenticationToken
				(loginDto.getName(), loginDto.getPassword());
		
		
		authenticationManager.authenticate(authenticates);
		
		String role = userDetailServiceImpl.getRole(loginDto.getName());
		String token =jwtUtil.generateToken(loginDto.getName(), role);
		
		return "done";
		
	}
	
	
	
	
	
	
	
	
	
}
