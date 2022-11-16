package com.carmarket.controller;

import com.carmarket.jwt.JwtConfiguration;
import com.carmarket.jwt.JwtTokenRequest;
import com.carmarket.jwt.JwtTokenResponse;
import com.carmarket.model.Customer;
import com.carmarket.exception.AuthenticationException;
import com.carmarket.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtConfiguration jwtConfiguration;
    private final CustomerService customerService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JwtConfiguration jwtConfiguration,
                                    CustomerService customerService) {
        this.authenticationManager = authenticationManager;
        this.jwtConfiguration = jwtConfiguration;
        this.customerService = customerService;
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtTokenRequest authenticationRequest)
            throws AuthenticationException {
        authenticate(authenticationRequest.getCustomerEmail(), authenticationRequest.getCustomerPassword());
        final UserDetails userDetails = customerService.loadUserByUsername(authenticationRequest.getCustomerEmail());
        final String token = jwtConfiguration.generateToken(userDetails);
        return ResponseEntity.ok(new JwtTokenResponse(token));
    }

    @GetMapping(value = "/refresh")
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String authToken = request.getHeader(jwtConfiguration.getTokenHeader());
        final String token = authToken.substring(7);
        String username = jwtConfiguration.getUsernameFromToken(token);
        Customer user = (Customer) customerService.loadUserByUsername(username);

        if (jwtConfiguration.canTokenBeRefreshed(token)) {
            String refreshedToken = jwtConfiguration.refreshToken(token);
            return ResponseEntity.ok(new JwtTokenResponse(refreshedToken));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @ExceptionHandler({ AuthenticationException.class })
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    private void authenticate(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new AuthenticationException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("INVALID_CREDENTIALS", e);
        }
    }
}
