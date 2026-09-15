package com.ait.app.dto;

import java.math.BigDecimal;

import lombok.Data;
@Data
public class OrderItemResponseDto {

    private Long menuItemId;

    private String itemName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    
}