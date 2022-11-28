package com.carmarket.model;

import com.carmarket.model.type.Currency;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "payment_id")
    private Long paymentId;
    @Column(name = "payment_date_time")
    private LocalDateTime paymentDateTime;
    @Column(name = "payment_amount")
    private Long paymentAmount;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_currency")
    private Currency paymentCurrency;
    @Column(name = "payment_email")
    private String paymentEmail;
    @Column(name = "payment_token")
    private String paymentToken;

    @Builder
    public Payment(LocalDateTime paymentDateTime, Long paymentAmount, Currency paymentCurrency,
                   String paymentEmail, String paymentToken) {
        super();
        this.paymentDateTime = paymentDateTime;
        this.paymentAmount = paymentAmount;
        this.paymentCurrency = paymentCurrency;
        this.paymentEmail = paymentEmail;
        this.paymentToken = paymentToken;
    }
}
