package com.ait.app.config;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.ait.app.entity.Role;
import com.stripe.model.identity.VerificationReport.Document.ExpirationDate;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private String secret = "yugwefuibefijgaergerg";

	public String generateToken(String name , String role) {

		return  Jwts.builder()
				.subject(name)
				.claim("role",role)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + 10000000000L))
				.signWith(getKeys()).compact();
						
	
	
	}

	public SecretKey getKeys() {
		
		
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));	}


public String extractUserName( String token) {
	
	return Jwts.parser().verifyWith(getKeys())
			.build()
			.parseSignedClaims(token)
			.getPayload().getSubject();
}

public String extractrole(String role)
{
	
	
	
	return getClaims(role).get("role", String.class);
}

private Claims getClaims(String token) {

	return Jwts.parser()

			.verifyWith(getKeys())

			.build()

			.parseSignedClaims(token)

			.getPayload();
}

}

