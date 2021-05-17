package com.cotiviti.service.impl;

import com.cotiviti.entities.BusDetails;
import com.cotiviti.entities.Customer;
import com.cotiviti.entities.Reservation;
import com.cotiviti.exception.BadRequestException;
import com.cotiviti.exception.UnauthorizedException;
import com.cotiviti.mapper.ReservationMapper;
import com.cotiviti.repository.BusDetailsRepository;
import com.cotiviti.repository.CustomerRepository;
import com.cotiviti.repository.ReservationRepository;
import com.cotiviti.request.ReservationRequest;
import com.cotiviti.response.Response;
import com.cotiviti.security.UserAuth;
import com.cotiviti.service.ReservationService;
import com.cotiviti.utils.ResponseUtility;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BusDetailsRepository busDetailsRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserAuth userAuth;

    @Override
    public Response reserveTicket(ReservationRequest reservationRequest) {
        validateReservationRequest(reservationRequest);
        try {
            BusDetails detail = busDetailsRepository.getBusDetails();
            validateSeat(reservationRequest, detail);
            Customer customer = customerRepository.findById(userAuth.getLoggedInUserId())
                    .orElseThrow(() -> new UnauthorizedException("Customer not found"));
            Reservation reservation = ReservationMapper.setReservationRequest(reservationRequest, detail, customer);
            reservationRepository.save(reservation);
            updateBusDetail(detail, reservationRequest);
            return ResponseUtility.getSuccessfulResponse(reservation.getId(), "Reservation Successful.");
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            return ResponseUtility.getFailedResponse("Server error");
        }
    }

    private void validateReservationRequest(ReservationRequest reservationRequest) {
        if (reservationRequest.getTravelMode().equalsIgnoreCase("Two Way")) {
            if (reservationRequest.getDepartureDate() == null) {
                throw new BadRequestException("Enter Departure Date");
            }
            if (reservationRequest.getReturnTime() == 0) {
                throw new BadRequestException("Enter Departure Date");
            }
        }
        if (reservationRequest.getKids() + reservationRequest.getAdults() + reservationRequest.getSeniour() <= 0) {
            throw new BadRequestException("At least one passenger is required.");
        }
    }

    private void validateSeat(ReservationRequest reservationRequest, BusDetails detail) throws BadRequestException {
        if (detail.getAvailableSeat() <= 0) {
            throw new BadRequestException("No seat available");
        }
        if (detail.getAvailableSeat() < reservationRequest.getTotalSeat()) {
            throw new BadRequestException("Only " + detail.getAvailableSeat() + " seat is available");
        }
    }

    private void updateBusDetail(BusDetails detail, ReservationRequest reservationRequest) {
        detail.setReservedSeat(detail.getReservedSeat() + reservationRequest.getTotalSeat());
        detail.setAvailableSeat(detail.getAvailableSeat() - reservationRequest.getTotalSeat());
        busDetailsRepository.save(detail);
    }

    @Override
    public Response getReservationData() {
        try {
            Customer customer = customerRepository.findById(userAuth.getLoggedInUserId())
                    .orElseThrow(() -> new UnauthorizedException("Customer not found"));
            List<Reservation> reservationList = reservationRepository.getAllReservation(customer.getId());
            return ResponseUtility.getSuccessfulResponse(ReservationMapper.setReservationResponse(reservationList), "Sucessfully fetched reservation summary");
        } catch (Exception e) {
            return ResponseUtility.getFailedResponse("Server error");
        }
    }

    @Override
    public Response getReservationByID(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Reservation not found"));
        try {
            return ResponseUtility.getSuccessfulResponse(ReservationMapper.setReservationResponse(reservation), "Sucessfully fetched reservation.");
        } catch (Exception e) {
            return ResponseUtility.getFailedResponse("Server error");
        }
    }
}
