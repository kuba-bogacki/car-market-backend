package com.carmarket.security;

import com.carmarket.jwt.JwtUnauthorizedResponseAuthentication;
import com.carmarket.jwt.TokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtUnauthorizedResponseAuthentication jwtUnauthorizedResponseAuthentication;
    private final UserDetailsService jwtInMemoryUserDetailsService;
    private final TokenVerifier tokenVerifier;
    private final PasswordConfigurer passwordConfigurer;

    @Autowired
    public ApplicationSecurityConfiguration(JwtUnauthorizedResponseAuthentication jwtUnauthorizedResponseAuthentication,
                                            UserDetailsService jwtInMemoryUserDetailsService,
                                            TokenVerifier tokenVerifier, PasswordConfigurer passwordConfigurer) {
        this.jwtUnauthorizedResponseAuthentication = jwtUnauthorizedResponseAuthentication;
        this.jwtInMemoryUserDetailsService = jwtInMemoryUserDetailsService;
        this.tokenVerifier = tokenVerifier;
        this.passwordConfigurer = passwordConfigurer;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticate) throws Exception {
        authenticate
                .userDetailsService(jwtInMemoryUserDetailsService)
                .passwordEncoder(passwordConfigurer.passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().and().authorizeRequests().and()
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(jwtUnauthorizedResponseAuthentication).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilterBefore(tokenVerifier, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/", "/css/*", "/js/*", "/authenticate", "/register", "/cars", "/get-car/*",
                        "/get-public-stripe-key", "/get-secret-stripe-key")
                .permitAll()
//                .antMatchers("/api/**").hasRole(ADMIN.name())
                .anyRequest().authenticated();
        httpSecurity
                .headers().cacheControl(HeadersConfigurer.CacheControlConfig::disable); //disable caching
    }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
//        source.registerCorsConfiguration("/**", corsConfiguration);
//        return source;
//    }
}
