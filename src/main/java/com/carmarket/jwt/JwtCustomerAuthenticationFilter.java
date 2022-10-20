//package com.carmarket.jwt;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.jsonwebtoken.Jwts;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import javax.crypto.SecretKey;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.Date;
//import java.util.stream.Collectors;
//
//public class JwtCustomerAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    private final AuthenticationManager authenticationManager;
//    private final JwtConfiguration jwtConfiguration;
//    private final SecretKey secretKey;
//
//    public JwtCustomerAuthenticationFilter(AuthenticationManager authenticationManager,
//                                   JwtConfiguration jwtConfiguration, SecretKey secretKey) {
//        this.authenticationManager = authenticationManager;
//        this.jwtConfiguration = jwtConfiguration;
//        this.secretKey = secretKey;
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request,
//                                                HttpServletResponse response) throws AuthenticationException {
//        try {
//            String text = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
//            String firstReplace = text.replaceAll("&", " ");
//            String secondReplace = firstReplace.replaceAll("=", " ");
//            String[] parts = secondReplace.split(" ");
//
//            String json = "{ \"username\" : \"" + parts[1] + "\", \"password\" : \"" + parts[3] + "\" }";
//
//            JwtTokenRequest authenticationRequest = new ObjectMapper()
//                    .readValue(json, JwtTokenRequest.class);
//            Authentication authentication = new UsernamePasswordAuthenticationToken(
//                    authenticationRequest.getCustomerEmail(), authenticationRequest.getCustomerPassword()
//            );
//            System.out.println(authenticationRequest.getCustomerEmail() + authenticationRequest.getCustomerPassword());
//            return authenticationManager.authenticate(authentication);
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request,
//                                            HttpServletResponse response,
//                                            FilterChain chain,
//                                            Authentication authResult) throws IOException, ServletException {
//        String token = Jwts.builder()
//                .setSubject(authResult.getName())
//                .claim("authorities", authResult.getAuthorities())
//                .setIssuedAt(new Date())
//                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusWeeks(1)))
//                .signWith(secretKey)
//                .compact();
//
//        System.out.println(token);
//        response.addHeader(jwtConfiguration.getAuthorizationHeader(), jwtConfiguration.getTokenPrefix() + token);
//    }
//}
