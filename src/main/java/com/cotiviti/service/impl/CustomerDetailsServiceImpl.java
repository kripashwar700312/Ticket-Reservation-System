package com.cotiviti.service.impl;

import com.cotiviti.entities.Customer;
import com.cotiviti.repository.CustomerRepository;
import com.cotiviti.service.AbstractUserDetailsService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class CustomerDetailsServiceImpl extends AbstractUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

}
