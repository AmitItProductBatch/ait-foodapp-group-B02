package com.ait.app.serviceImplExtra;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ait.app.dto.PaymentRequestDto;
import com.ait.app.entity.Payment;
import com.ait.app.exception.PaymentException;
import com.ait.app.repository.PaymentRepository;
import com.ait.app.service.PaymentService;

import jakarta.transaction.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	PaymentRepository paymentRepository;

	@Override
	public Payment addPayment(PaymentRequestDto dto) {

		Payment payment = new Payment();

		payment.setTransactionId(dto.getTransactionId());
		payment.setOrderId(dto.getOrderId());
		payment.setUserId(dto.getUserId());
		payment.setAmount(dto.getAmount());
		payment.setPaymentMethod(dto.getPaymentMethod());
		payment.setPaymentStatus(dto.getPaymentStatus());

		payment.setPaymentDate(LocalDateTime.now());

		return paymentRepository.save(payment);
	}

	@Override
	public Payment getPayment(Long id) {
		Payment payment = paymentRepository.findById(id).orElse(null);

		if (payment == null) {
			throw new PaymentException("Payment not found", HttpStatus.NOT_FOUND);
		}
		return payment;
	}

	@Override
	public List<Payment> getAllPayments() {
		return paymentRepository.findAll();
	}

	@Override
	public void deletePayment(Long id) {
		Payment payment = paymentRepository.findById(id).orElse(null);

		if (payment == null) {
			throw new PaymentException("Payment not found", HttpStatus.NOT_FOUND);
		}
		paymentRepository.deleteById(id);
	}

	@Override
	@Transactional
	public Payment updatePaymentStatus(Long id, String status) {

		Payment payment = paymentRepository.findById(id).orElse(null);

		if (payment == null) {
			throw new PaymentException("Payment not found", HttpStatus.NOT_FOUND);
		}

		paymentRepository.updatePaymentStatus(id, status);

		return paymentRepository.findById(id).get();
	}

}
