package com.cotiviti.service;

import com.cotiviti.entities.Customer;
import com.cotiviti.exception.UnauthorizedException;
import com.cotiviti.repository.CustomerRepository;
import com.cotiviti.repository.RoleGroupMapRepository;
import com.cotiviti.security.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractUserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoleGroupMapRepository roleGroupMapRepository;

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        Optional<Customer> customerOpt = customerRepository.findByUsername(username);
        if (!customerOpt.isPresent()) {
            throw new UnauthorizedException("Invalid Username or Password.");
        }
        Customer customer = customerOpt.get();
        return new JwtUser(customer, getAuthority(customer));
    }

    private Set<SimpleGrantedAuthority> getAuthority(Customer customer) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        roleGroupMapRepository.getRolePermissionsByGroupName(customer.getUserGroup().getName())
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority("ROLE_" + permission)));
        return authorities;
    }
}
