package com.cotiviti.security;

import com.cotiviti.entities.Customer;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtUser implements UserDetails {

    private final Customer customer;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUser(Customer customer) {
        this(customer, new ArrayList<>());
    }

    public JwtUser(Customer customer, Collection<? extends GrantedAuthority> authorities) {
        this.customer = customer;
        this.authorities = authorities;
    }

    @JsonIgnore
    public Long getId() {
        return customer.getId();
    }

    @Override
    public String getUsername() {
        return customer.getUsername();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @JsonIgnore
    public String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Customer getUser() {
        return customer;
    }
}
