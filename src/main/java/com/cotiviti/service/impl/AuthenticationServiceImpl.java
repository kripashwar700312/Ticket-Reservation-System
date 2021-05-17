package com.cotiviti.service.impl;

import com.cotiviti.constants.JwtTokenConstants;
import com.cotiviti.constants.MetatableConstant;
import com.cotiviti.constants.StatusConstant;
import com.cotiviti.entities.Customer;
import com.cotiviti.exception.BadRequestException;
import com.cotiviti.exception.UnauthorizedException;
import com.cotiviti.repository.CustomerRepository;
import com.cotiviti.repository.StatusRepository;
import com.cotiviti.request.JwtAuthenticationRequest;
import com.cotiviti.response.Response;
import com.cotiviti.service.AuthenticationService;
import com.cotiviti.service.JwtTokenService;
import com.cotiviti.utils.ResponseUtility;
import com.cotiviti.security.JwtUser;
import com.cotiviti.security.UserAuth;
import com.nimbusds.jwt.JWTClaimsSet;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private CustomerDetailsServiceImpl customerDetailsServiceImpl;

    @Autowired
    private UserAuth userAuth;

    @Override
    public ResponseEntity<Response> authenticate(JwtAuthenticationRequest authenticationRequest) {
        Customer customer = customerRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Username doesn't exist."));
        if (!passwordEncoder.matches(authenticationRequest.getPassword(), customer.getPassword())) {
            increaseWrongPasswordAttempt(customer);
            throw new UsernameNotFoundException("Invalid Username or Password.");
        }
        resetWrongPasswordAttempt(customer);
        String token = generateToken(customer);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, token);
        Response response = ResponseUtility.getSuccessfulResponse("Logged In Successfully.");
        return new ResponseEntity<>(response, headers, response.getHttpStatus());
    }

    private void increaseWrongPasswordAttempt(Customer customer) {
        customer.setWrongPasswordAttemptCount(customer.getWrongPasswordAttemptCount() + 1);
        if (customer.getWrongPasswordAttemptCount() < MetatableConstant.WRONG_PASSWORD_ATTEMPT_ALLOWED) {
            if (customer.getWrongPasswordAttemptCount() == MetatableConstant.WRONG_PASSWORD_ATTEMPT_ALLOWED) {
                customer.setStatus(statusRepository.getByName(StatusConstant.BLOCKED_APPROVE.getName()));
            }
            customerRepository.save(customer);
            showMessageforWrongPasswordAttemptsLeft(MetatableConstant.WRONG_PASSWORD_ATTEMPT_ALLOWED, customer);
        }
    }

    private void showMessageforWrongPasswordAttemptsLeft(int wrongPasswordAttemptsAllowed, Customer customer) throws NumberFormatException {
        int passwordAttemptsLeft = wrongPasswordAttemptsAllowed - customer.getWrongPasswordAttemptCount();
        if (passwordAttemptsLeft == 0) {
            throw new UnauthorizedException("Your account has been blocked due to too many wrong login attempts. Please try again later.");
        } else {
            throw new UnauthorizedException("Login failed: " + passwordAttemptsLeft + " attempts left!");
        }
    }

    private void resetWrongPasswordAttempt(Customer customer) {
        customer.setWrongPasswordAttemptCount(0);
        customer.setLoginExpired(false);
        customerRepository.save(customer);
    }

    public String generateToken(Customer customer) {
        final JwtUser jwtUser = (JwtUser) customerDetailsServiceImpl.loadUserByUsername(customer.getUsername());
        final String token = jwtTokenService.generateToken(getClaims(jwtUser, customer), new Date());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(jwtUser, "", jwtUser.getAuthorities()));
        jwtUser.setToken(token);
        return token;
    }

    private JWTClaimsSet getClaims(JwtUser jwtUser, Customer customer) {
        return new JWTClaimsSet.Builder()
                .audience(JwtTokenConstants.USER.getName())
                .subject(JwtTokenConstants.AUTH.getName())
                .issuer(JwtTokenConstants.TICKET_RESERVATION_SYSTEM.getName())
                .claim(JwtTokenConstants.USER_ID.getName(), customer.getId())
                .claim(JwtTokenConstants.USERNAME.getName(), customer.getUsername())
                .claim(JwtTokenConstants.GROUP.getName(), customer.getUserGroup().getName())
                .claim(JwtTokenConstants.ROLES.getName(), getRoles(jwtUser))
                .build();
    }

    private String getRoles(JwtUser jwtUser) {
        return ((Collection<? extends GrantedAuthority>) jwtUser.getAuthorities())
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    @Override
    public Response logout() {
        Customer customer = customerRepository.findByUsername(userAuth.getUsername())
                .orElseThrow(() -> new BadRequestException("Something went wrong."));
        customer.setLoginExpired(true);
        customerRepository.save(customer);
        return ResponseUtility.getSuccessfulResponse("Logout Successfully.");
    }
}
