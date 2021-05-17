package com.cotiviti.service;

import com.cotiviti.request.JwtAuthenticationRequest;
import com.cotiviti.response.Response;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {

    ResponseEntity<Response> authenticate(JwtAuthenticationRequest authenticationRequest);

    Response logout();
}
