package com.carmarket.stripe;

import com.stripe.Stripe;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Configuration
@PropertySource(value = {"classpath:application.properties"})
public class PaymentConfiguration implements Serializable {

    @Serial
    private static final long serialVersionUID = -3301605591108950415L;
    private String stripeSecretKey = Stripe.apiKey;

    @Autowired
    public PaymentConfiguration(
            @Value("${stripe.secret.key:}") String stripeSecretKey) {
        Stripe.apiKey = stripeSecretKey;
    }
}
