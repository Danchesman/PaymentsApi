package com.example.paymentsapi;

import com.example.paymentsapi.model.DecimalNumber;
import com.example.paymentsapi.model.Payment;
import com.example.paymentsapi.model.PaymentStatus;
import com.example.paymentsapi.repositories.PaymentRepository;
import com.example.paymentsapi.services.PaymentService;
import com.example.paymentsapi.utils.PaymentNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PaymentsApiApplicationTests {

    @Autowired
    private PaymentService paymentService;

    @MockBean
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("Модульный тест: проверка создания платежа")
    void testCreatePayment() {
        DecimalNumber amount = new DecimalNumber(1000, 2);
        String clientName = "John Doe";
        Payment expectedPayment = new Payment(UUID.randomUUID(), 1000, 2, PaymentStatus.Begin, clientName, new Date().getTime());

        when(paymentRepository.save(any(Payment.class))).thenReturn(expectedPayment);

        Payment actualPayment = paymentService.create(amount, clientName);

//        assertEquals(expectedPayment, actualPayment);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Модульный тест: проверка обновления статуса платежа")
    void testUpdatePaymentStatus() throws PaymentNotFoundException {
        UUID paymentId = UUID.randomUUID();
        PaymentStatus newStatus = PaymentStatus.Committed;
        Payment existingPayment = new Payment(paymentId, 1000, 2, PaymentStatus.Begin, "John Doe", new Date().getTime());

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(existingPayment));

        Payment updatedPayment = paymentService.update(paymentId, newStatus);

        assertEquals(newStatus, updatedPayment.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Модульный тест: проверка получения всех платежей")
    void testGetAllPayments() {
        Pageable pageable = PageRequest.of(0, 10);
        Payment payment1 = new Payment(UUID.randomUUID(), 1000, 2, PaymentStatus.Begin, "John Doe", new Date().getTime());
        Payment payment2 = new Payment(UUID.randomUUID(), 2000, 2, PaymentStatus.PreCommit, "Jane Doe", new Date().getTime());
        Page<Payment> expectedPage = new PageImpl<>(Arrays.asList(payment1, payment2), pageable, 2);

        when(paymentRepository.findAll(pageable)).thenReturn(expectedPage);

        Payment[] actualPayments = paymentService.getAll(pageable);

        assertEquals(2, actualPayments.length);
        assertArrayEquals(new Payment[]{payment1, payment2}, actualPayments);
        verify(paymentRepository, times(1)).findAll(pageable);
    }
}