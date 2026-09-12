package com.ait.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ait.app.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	
	Payment findPaymentById(Long id);
	
	 @Modifying
	    @Query(value = """
	            UPDATE payments
	            SET payment_status = :status
	            WHERE id = :id
	            """, nativeQuery = true)
	    int updatePaymentStatus(
	            @Param("id") Long id,
	            @Param("status") String status);

}
