package com.ait.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SequrityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/users").permitAll()
			    .requestMatchers("/api/roles/**").permitAll()
			    .requestMatchers("/api/admin/**").hasRole("ADMIN")
			    .requestMatchers("/api/owner/**").hasRole("OWNER")
			    .requestMatchers("/api/user/**").hasRole("CUSTOMER")
			    .requestMatchers("/api/delivery/**").hasRole("DELIVERY_BOY")
			    .anyRequest().authenticated()
			)
		.httpBasic(Customizer.withDefaults());
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
