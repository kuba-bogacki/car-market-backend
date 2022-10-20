package com.carmarket.security;

import com.carmarket.jwt.JwtUnauthorizedResponseAuthentication;
import com.carmarket.jwt.TokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.carmarket.model.type.CustomerRole.ADMIN;

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
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(jwtUnauthorizedResponseAuthentication).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*", "/authenticate", "/temp-new-user")
                .permitAll()
                .antMatchers("/api/**").hasRole(ADMIN.name())
                .anyRequest().authenticated();
        httpSecurity
                .addFilterBefore(tokenVerifier, UsernamePasswordAuthenticationFilter.class);
        httpSecurity
                .headers()
                .cacheControl(); //disable caching
    }

//    @Override
//    public void configure(WebSecurity webSecurity) throws Exception {
//        webSecurity
//                .ignoring()
//                .antMatchers(HttpMethod.POST, "/authenticate")
//                .antMatchers(HttpMethod.OPTIONS, "/**")
//                .and()
//                .ignoring()
//                .antMatchers(HttpMethod.GET, "/") //Other Stuff You want to Ignore
//                .and()
//                .ignoring()
//                .antMatchers("/h2-console/**/**");//Should not be in Production!
//    }
}
