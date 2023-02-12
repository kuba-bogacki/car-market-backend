package com.carmarket.service;

import com.carmarket.model.Customer;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailService {
    void sendVerificationEmail(Customer customer) throws MessagingException, IOException;
}
