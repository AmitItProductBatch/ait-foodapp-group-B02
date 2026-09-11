package com.ait.app.serviceImplExtra;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.PaymentRequestDto;
import com.ait.app.entity.Payment;
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
	        return paymentRepository.findById(id).get();
	    }

	    @Override
	    public List<Payment> getAllPayments() {
	        return paymentRepository.findAll();
	    }

	    @Override
	    public void deletePayment(Long id) {
	        paymentRepository.deleteById(id);
	    }

	    @Override
	    @Transactional
	    public Payment updatePaymentStatus(Long id, String status) {

	        paymentRepository.updatePaymentStatus(id, status);

	        return paymentRepository.findById(id).get();
	    }
 

}
