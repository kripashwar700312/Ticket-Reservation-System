package com.cotiviti.controller;

import com.cotiviti.annotation.AllowTemp;
import com.cotiviti.constants.APIConstant;
import com.cotiviti.request.JwtAuthenticationRequest;
import com.cotiviti.response.Response;
import com.cotiviti.service.AuthenticationService;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = APIConstant.AUTH)
@CrossOrigin("*")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @AllowTemp
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> authenticate(@NotNull @Valid @RequestBody JwtAuthenticationRequest authenticationRequest) {
        return authenticationService.authenticate(authenticationRequest);
    }

    @GetMapping(path = APIConstant.LOGOUT)
    public ResponseEntity<Response> logout() {
        Response response = authenticationService.logout();
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
