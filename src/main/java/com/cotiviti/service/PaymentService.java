package com.cotiviti.service;

import com.cotiviti.request.PaymentRequest;
import com.cotiviti.response.Response;

public interface PaymentService {

    Response pay(PaymentRequest paymentRequest);
}
