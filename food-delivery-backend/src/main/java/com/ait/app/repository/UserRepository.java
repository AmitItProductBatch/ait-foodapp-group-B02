package com.ait.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ait.app.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	boolean existsByEmail(String email);

	boolean existsByMobile(String mobile);

	@Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :id")
	boolean existsEmailForOtherUser(@Param("email") String email, @Param("id") int id);

	
	@Query("SELECT COUNT(u) > 0 FROM User u WHERE u.mobile = :mobile AND u.id != :id")
	boolean existsMobileForOtherUser(@Param("mobile") String mobile, @Param("id") int id);
}
