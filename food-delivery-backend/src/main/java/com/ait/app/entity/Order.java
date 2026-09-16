package com.ait.app.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    private BigDecimal subtotal;

  //  private BigDecimal discount;
    
    private String deliveryAddress;

    private BigDecimal tax;

    private BigDecimal deliveryFee;

    private BigDecimal packagingFee;

    private BigDecimal totalAmount;

  
    private String orderStatus;//PLACED,CONFIRMED,PREPARING,OUT_FOR_DELIVERY,DELIVERED,CANCELLED

    private String paymentStatus;//success,failed,prosessing

    private String paymentMethod;//upi,wallet,cod

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

	
}