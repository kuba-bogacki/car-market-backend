package com.carmarket.service;

import com.carmarket.model.Payment;
import com.carmarket.model.type.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

public interface PaymentService {
    List<Payment> getAllTransactions();
    Charge chargeCustomerCard(Long paymentAmount, Currency paymentCurrency,
            String paymentEmail, String paymentToken) throws AuthenticationException, StripeException;
}
