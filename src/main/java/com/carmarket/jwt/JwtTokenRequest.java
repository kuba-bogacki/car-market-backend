package com.carmarket.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -5616176897013108345L;
    private String customerEmail;
    private String customerPassword;
}
