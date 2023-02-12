package com.carmarket.security;

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
public class GoogleConfiguration implements Serializable {

    @Serial
    private static final long serialVersionUID = -3301605591108950415L;
    private String googleClientId;
    private String googleClientSecret;

    @Autowired
    public GoogleConfiguration(
            @Value("${spring.security.oauth2.client.registration.google.client-id:}") String googleClientId,
            @Value("${spring.security.oauth2.client.registration.google.client-secret:}") String googleClientSecret) {
        this.googleClientId = googleClientId;
        this.googleClientSecret = googleClientSecret;
    }
}
