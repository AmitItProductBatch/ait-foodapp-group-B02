package com.ait.app.dto;


import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentInitiationResponseDto {
    private Long paymentId;
    private String transactionId;     
    private String clientSecret;      
    private Long orderId;
    private Double amount;
    private String paymentStatus;
    private LocalDateTime paymentDate;
}