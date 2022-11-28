package com.carmarket.service.implementation;

import com.carmarket.model.Payment;
import com.carmarket.model.type.Currency;
import com.carmarket.repository.PaymentRepository;
import com.carmarket.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
class PaymentServiceImplementation implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentServiceImplementation(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Charge chargeCustomerCard(Long paymentAmount, Currency paymentCurrency,
                 String paymentEmail, String paymentToken) throws AuthenticationException, StripeException {

        Payment payment = Payment.builder()
                .paymentDateTime(LocalDateTime.now())
                .paymentAmount(paymentAmount)
                .paymentCurrency(paymentCurrency)
                .paymentEmail(paymentEmail)
                .paymentToken(paymentToken)
                .build();

        paymentRepository.save(payment);

        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", payment.getPaymentAmount());
        chargeParams.put("currency", payment.getPaymentCurrency());
        chargeParams.put("source", payment.getPaymentToken());
        return Charge.create(chargeParams);
    }
}
