package com.carmarket.service;

import com.carmarket.model.type.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.security.core.AuthenticationException;

public interface PaymentService {
    Charge chargeCustomerCard(Long paymentAmount, Currency paymentCurrency,
            String paymentEmail, String paymentToken) throws AuthenticationException, StripeException;
}
