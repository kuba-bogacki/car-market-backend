package com.carmarket.service.implementation;

import com.carmarket.model.Customer;
import com.carmarket.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Service
class EmailServiceImplementation implements EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailServiceImplementation(@Lazy JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("car.market.organisation@gmail.com");
        mailSender.setPassword("owitssojsqeclwhk");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    public String fileFormatterAndReader(String text) throws IOException {

        File fileDirectory = new File(text);
        FileInputStream fileInputStream = new FileInputStream(fileDirectory);
        Charset inputCharset = StandardCharsets.ISO_8859_1;
        BufferedReader in = new BufferedReader(new InputStreamReader(fileInputStream, inputCharset));

        String line;
        StringBuilder emailBody = new StringBuilder();

        while ((line = in.readLine()) != null) {
            emailBody.append(line);
        }
        return emailBody.toString();
    }

    @Override
    public void sendVerificationEmail(Customer customer) throws MessagingException, IOException {

        String textRoute = ".\\src\\main\\resources\\static\\emails\\reset-password.txt";
        String content = fileFormatterAndReader(textRoute);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("car.market.organisation@gmail.com", "Car Market");
        helper.setTo(customer.getCustomerEmail());
        helper.setSubject("Dear customer, now you can reset your password");
        content = content.replace("[[name]]", customer.getCustomerFirstName() + " " + customer.getCustomerLastName());
        String verifyURL = "http://localhost:3000/reset-password?customer-verification-code=" + customer.getCustomerVerificationCode();
        content = content.replace("[[URL]]", verifyURL);
        helper.setText(content, true);

        javaMailSender.send(message);
    }
}
