package com.carmarket.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "customer")
public class Customer implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "customer_first_name")
    private String customerFirstName;
    @Column(name = "customer_last_name")
    private String customerLastName;
    @Column(name = "customer_email", unique = true)
    private String customerEmail;
    @Column(name = "customer_password")
    private String customerPassword;
    @OneToMany
    @Column(name = "customer_cars_list")
    private List<Car> customerCarsList = new ArrayList<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "authorities")
    private Set<SimpleGrantedAuthority> authorities;
    @Column(name = "account_not_expired")
    private boolean accountNonExpired;
    @Column(name = "account_not_locked")
    private boolean accountNonLocked;
    @Column(name = "credential_not_expired")
    private boolean credentialsNonExpired;
    @Column(name = "enabled")
    private boolean enabled;

    @Builder
    public Customer(String customerFirstName, String customerLastName, String customerEmail, String customerPassword,
                    Set<SimpleGrantedAuthority> authorities, boolean accountNonExpired, boolean accountNonLocked,
                    boolean credentialsNonExpired, boolean enabled) {
        super();
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerPassword = customerPassword;
        this.customerEmail = customerEmail;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.customerPassword;
    }

    @Override
    public String getUsername() {
        return this.customerEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
