package com.cotiviti.service.impl;

import com.cotiviti.entities.Reservation;
import com.cotiviti.exception.BadRequestException;
import com.cotiviti.mapper.PaymentLogMapper;
import com.cotiviti.repository.PaymentLogRepository;
import com.cotiviti.repository.ReservationRepository;
import com.cotiviti.request.PaymentRequest;
import com.cotiviti.response.Response;
import com.cotiviti.security.UserAuth;
import com.cotiviti.service.PaymentService;
import com.cotiviti.utils.ResponseUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserAuth userAuth;

    @Autowired
    private PaymentLogRepository paymentLogRepository;

    @Override
    public Response pay(PaymentRequest paymentRequest) {
        Reservation reservation = reservationRepository.getByIdAndCustomerId(paymentRequest.getReservationId(), userAuth.getLoggedInUserId())
                .orElseThrow(() -> new BadRequestException("Reservarion not found"));
        if (!reservation.isPaid() && makePayment(paymentRequest, reservation.getTotalPrice())) {
            paymentLogRepository.save(PaymentLogMapper.setPaymentLog(reservation));
            reservation.setPaid(true);
            reservationRepository.save(reservation);
        }
        return ResponseUtility.getSuccessfulResponse("Successfully made payment.");
    }

    //Dummy Payment Gateway
    private boolean makePayment(PaymentRequest paymentRequest, Double payableAmount) {
        return true;
    }

}
