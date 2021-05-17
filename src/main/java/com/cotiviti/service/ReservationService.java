package com.cotiviti.service;

import com.cotiviti.request.ReservationRequest;
import com.cotiviti.response.Response;

public interface ReservationService {

    Response reserveTicket(ReservationRequest reservationRequest);

    Response getReservationData();

    Response getReservationByID(Long id);

}
