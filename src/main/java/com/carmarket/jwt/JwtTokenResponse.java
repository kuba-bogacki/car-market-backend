package com.carmarket.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@AllArgsConstructor
public class JwtTokenResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 8317676219297719109L;
    private final String token;

}
