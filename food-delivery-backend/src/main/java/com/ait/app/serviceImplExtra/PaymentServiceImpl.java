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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	PaymentRepository paymentRepository;

	@Override
	public Payment addPayment(PaymentRequestDto dto) {

		log.info("Creating payment for orderId: {}", dto.getOrderId());

		Payment payment = new Payment();

		payment.setTransactionId(dto.getTransactionId());
		payment.setOrderId(dto.getOrderId());
		payment.setUserId(dto.getUserId());
		payment.setAmount(dto.getAmount());
		payment.setPaymentMethod(dto.getPaymentMethod());
		payment.setPaymentStatus(dto.getPaymentStatus());

		payment.setPaymentDate(LocalDateTime.now());

		Payment savedPayment = paymentRepository.save(payment);

		log.info("Payment created successfully with id: {}",
				savedPayment.getId());

		return savedPayment;
	}

	@Override
	public Payment getPayment(Long id) {

		log.debug("Fetching payment with id: {}", id);

		Payment payment = paymentRepository.findById(id).orElse(null);

		if (payment == null) {

			log.warn("Payment not found with id: {}", id);

			throw new PaymentException(
					"Payment not found",
					HttpStatus.NOT_FOUND);
		}

		log.info("Payment found with id: {}", id);

		return payment;
	}

	@Override
	public List<Payment> getAllPayments() {

		log.debug("Fetching all payments");

		long startTime = System.currentTimeMillis();

		List<Payment> payments = paymentRepository.findAll();

		long executionTime =
				System.currentTimeMillis() - startTime;

		log.info("Payment search completed in {} ms",
				executionTime);

		if (payments.isEmpty()) {
			log.warn("No payments found");
		} else {
			log.info("{} payments found", payments.size());
		}

		return payments;
	}

	@Override
	public void deletePayment(Long id) {

		log.info("Deleting payment with id: {}", id);

		Payment payment = paymentRepository.findById(id).orElse(null);

		if (payment == null) {

			log.warn("Cannot delete. Payment not found with id: {}",
					id);

			throw new PaymentException(
					"Payment not found",
					HttpStatus.NOT_FOUND);
		}

		paymentRepository.deleteById(id);

		log.info("Payment deleted successfully with id: {}", id);
	}

	@Override
	@Transactional
	public Payment updatePaymentStatus(Long id, String status) {

		log.info("Updating payment status for id: {} to {}",
				id, status);

		Payment payment = paymentRepository.findById(id).orElse(null);

		if (payment == null) {

			log.warn("Cannot update payment status. Payment not found with id: {}",
					id);

			throw new PaymentException(
					"Payment not found",
					HttpStatus.NOT_FOUND);
		}

		log.debug("Current payment status for id {}: {}",
				id, payment.getPaymentStatus());

		paymentRepository.updatePaymentStatus(id, status);

		Payment updatedPayment =
				paymentRepository.findById(id).get();

		log.info("Payment status updated successfully for id: {} from {} to {}",
				id,
				payment.getPaymentStatus(),
				status);

		return updatedPayment;
	}

}