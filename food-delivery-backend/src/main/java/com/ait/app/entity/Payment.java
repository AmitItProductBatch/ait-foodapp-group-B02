package com.ait.app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "payments")
public class Payment {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false, unique = true)
	    private String transactionId;

	    @OneToOne
	    @JoinColumn(name = "order_id", nullable = false, unique = true)
	    private Order order;

	    @Column(nullable = false)
	    private double amount;

	    @Column(nullable = false)
	    private String paymentMethod;

	    @Column(nullable = false)
	    private String paymentStatus;

	    private LocalDateTime paymentDate;

}
