package com.ait.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.PaymentRequestDto;
import com.ait.app.entity.Payment;
import com.ait.app.service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {
	
	@Autowired
    PaymentService paymentService;

    @PostMapping("/add")
    ResponseEntity<Payment> addPayment(@RequestBody PaymentRequestDto dto) {

        Payment payment = paymentService.addPayment(dto);

        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    ResponseEntity<Payment> getPayment(@PathVariable Long id) {

        Payment payment = paymentService.getPayment(id);

        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @GetMapping("/all")
    ResponseEntity<List<Payment>> getAllPayments() {

        List<Payment> payments = paymentService.getAllPayments();

        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<String> deletePayment(@PathVariable Long id) {

        paymentService.deletePayment(id);

        return new ResponseEntity<>("Payment deleted", HttpStatus.OK);
    }

    @PutMapping("/update-status/{id}")
    ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Payment payment = paymentService.updatePaymentStatus(id, status);

        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

}
