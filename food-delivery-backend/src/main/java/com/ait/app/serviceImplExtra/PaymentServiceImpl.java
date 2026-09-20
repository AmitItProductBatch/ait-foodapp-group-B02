package com.ait.app.serviceImplExtra;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ait.app.dto.PaymentInitiationResponseDto;
import com.ait.app.dto.PaymentRequestDto;
import com.ait.app.entity.Payment;
import com.ait.app.exception.PaymentException;
import com.ait.app.repository.PaymentRepository;
import com.ait.app.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final StripeGatewayService stripeGatewayService;

    @Override
    @Transactional
    public PaymentInitiationResponseDto initiatePayment(PaymentRequestDto dto) {
        log.info("Initiating payment for orderId: {}", dto.getOrderId());

        try {
            // Stripe expects amount in smallest currency unit (cents)
            long amountInSmallestUnit = Math.round(dto.getAmount() * 100);

            PaymentIntent intent = stripeGatewayService.createPaymentIntent(
                amountInSmallestUnit, 
                "usd"
            );

            Payment payment = new Payment();
            payment.setTransactionId(intent.getId());
            payment.setOrderId(dto.getOrderId());
            payment.setUserId(dto.getUserId());
            payment.setAmount(dto.getAmount());
            payment.setPaymentMethod("STRIPE");
            payment.setPaymentStatus(intent.getStatus().toUpperCase());
            payment.setPaymentDate(LocalDateTime.now());

            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment record created with id: {} and transactionId: {}", 
                     savedPayment.getId(), intent.getId());

            return PaymentInitiationResponseDto.builder()
                    .paymentId(savedPayment.getId())
                    .transactionId(savedPayment.getTransactionId())
                    .clientSecret(intent.getClientSecret())
                    .orderId(savedPayment.getOrderId())
                    .amount(savedPayment.getAmount())
                    .paymentStatus(savedPayment.getPaymentStatus())
                    .paymentDate(savedPayment.getPaymentDate())
                    .build();

        } catch (StripeException e) {
            log.error("Stripe gateway failure for orderId {}: {}", dto.getOrderId(), e.getMessage());
            throw new PaymentException("Payment gateway error: " + e.getUserMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPayment(Long id) {
        log.debug("Fetching payment with id: {}", id);

        Payment payment = paymentRepository.findById(id).orElse(null);

        if (payment == null) {
            log.warn("Payment not found with id: {}", id);
            throw new PaymentException("Payment not found", HttpStatus.NOT_FOUND);
        }

        log.info("Payment found with id: {}", id);
        return payment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        log.debug("Fetching all payments");
        List<Payment> payments = paymentRepository.findAll();

        if (payments.isEmpty()) {
            log.warn("No payments found");
        } else {
            log.info("{} payment(s) found", payments.size());
        }

        return payments;
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        log.info("Deleting payment with id: {}", id);

        Payment payment = paymentRepository.findById(id).orElse(null);

        if (payment == null) {
            log.warn("Cannot delete. Payment not found with id: {}", id);
            throw new PaymentException("Payment not found", HttpStatus.NOT_FOUND);
        }

        paymentRepository.deleteById(id);
        log.info("Payment deleted successfully with id: {}", id);
    }

    @Override
    @Transactional
    public Payment updatePaymentStatus(Long id, String status) {
        log.info("Updating payment status for id: {} to {}", id, status);

        Payment payment = paymentRepository.findById(id).orElse(null);

        if (payment == null) {
            log.warn("Cannot update payment status. Payment not found with id: {}", id);
            throw new PaymentException("Payment not found", HttpStatus.NOT_FOUND);
        }

        log.debug("Current payment status for id {}: {}", id, payment.getPaymentStatus());

        String previousStatus = payment.getPaymentStatus();
        payment.setPaymentStatus(status);

        Payment updatedPayment = paymentRepository.save(payment);

        log.info("Payment status updated successfully for id: {} from {} to {}",
                id, previousStatus, status);

        return updatedPayment;
    }

	
}