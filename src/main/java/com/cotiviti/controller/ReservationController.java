package com.cotiviti.controller;

import com.cotiviti.constants.APIConstant;
import static com.cotiviti.constants.RolesConstants.RESERVE_TICKET;
import static com.cotiviti.constants.RolesConstants.VIEW_RESERVED_TICKET;
import com.cotiviti.request.ReservationRequest;
import com.cotiviti.response.Response;
import com.cotiviti.service.ReservationService;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = APIConstant.RESERVATION)
@CrossOrigin("*")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PreAuthorize(RESERVE_TICKET)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> reserveTicket(@NotNull @Valid @RequestBody ReservationRequest reservationRequest) {
        Response response = reservationService.reserveTicket(reservationRequest);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PreAuthorize(VIEW_RESERVED_TICKET)
    @GetMapping
    public ResponseEntity<Response> getReservationData() {
        Response response = reservationService.getReservationData();
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PreAuthorize(VIEW_RESERVED_TICKET)
    @PostMapping(APIConstant.ID)
    public ResponseEntity<Response> getReservationById(@NotNull @Valid @RequestBody Long id) {
        Response response = reservationService.getReservationByID(id);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
