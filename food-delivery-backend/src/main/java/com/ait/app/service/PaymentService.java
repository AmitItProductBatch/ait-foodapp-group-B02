package com.ait.app.service;

import java.util.List;

import com.ait.app.dto.PaymentRequestDto;
import com.ait.app.entity.Payment;

public interface PaymentService {
	
	Payment addPayment(PaymentRequestDto dto);

    Payment getPayment(Long id);

    List<Payment> getAllPayments();

    void deletePayment(Long id);

    Payment updatePaymentStatus(Long id, String status);

}
