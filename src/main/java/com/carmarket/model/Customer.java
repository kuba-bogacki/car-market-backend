package com.carmarket.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

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
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> customerCarsList = new ArrayList<>();
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "customers_cars",
            joinColumns = {@JoinColumn(name = "customer_id")},
            inverseJoinColumns = {@JoinColumn(name = "car_id")})
    private Set<Car> customerLikes = new HashSet<>();
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Article> customerArticles = new ArrayList<>();
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
                    List<Car> customerCarsList, Set<Car> customerLikes, List<Article> customerArticles,
                    Set<SimpleGrantedAuthority> authorities, boolean accountNonExpired, boolean accountNonLocked,
                    boolean credentialsNonExpired, boolean enabled) {
        super();
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerPassword = customerPassword;
        this.customerCarsList = customerCarsList;
        this.customerLikes = customerLikes;
        this.customerArticles = customerArticles;
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

    public Customer addCarToCustomerSet(Car car) {
        this.customerLikes.add(car);
        return this;
    }

    public Customer removeCarFromCustomerSet(Car car) {
        this.customerLikes.remove(car);
        return this;
    }
}
