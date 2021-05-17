package com.cotiviti.controller;

import com.cotiviti.constants.APIConstant;
import static com.cotiviti.constants.RolesConstants.PAYMENT;
import com.cotiviti.request.PaymentRequest;
import com.cotiviti.response.Response;
import com.cotiviti.service.PaymentService;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = APIConstant.PAYMENT)
@CrossOrigin("*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PreAuthorize(PAYMENT)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> authenticate(@NotNull @Valid @RequestBody PaymentRequest paymentRequest) {
        Response response = paymentService.pay(paymentRequest);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
