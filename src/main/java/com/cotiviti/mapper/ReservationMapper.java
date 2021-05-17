package com.cotiviti.mapper;

import com.cotiviti.entities.BusDetails;
import com.cotiviti.entities.Customer;
import com.cotiviti.entities.Reservation;
import com.cotiviti.request.ReservationRequest;
import com.cotiviti.response.ReservationResponse;
import com.cotiviti.response.ReservationSummary;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationMapper {
    
    public static Reservation setReservationRequest(ReservationRequest reservationRequest, BusDetails busDetails, Customer customer) {
        Reservation reservation = new Reservation();
        reservation.setTravelMode(reservationRequest.getTravelMode());
        reservation.setFromCity(reservationRequest.getFromCity());
        reservation.setToCity(reservationRequest.getToCity());
        reservation.setDepartureDate(reservationRequest.getDepartureDate());
        reservation.setReturnDate(reservationRequest.getReturnDate());
        reservation.setDepartureTime(reservationRequest.getDepartureTime());
        reservation.setReturnTime(reservationRequest.getReturnTime());
        reservation.setKids(reservationRequest.getKids());
        reservation.setAdults(reservationRequest.getAdults());
        reservation.setSeniour(reservationRequest.getSeniour());
        reservation.setReservationDate(new Date());
        reservation.setBusDetails(busDetails);
        reservation.setCustomer(customer);
        reservation.setTotalPrice(busDetails.getPricePerSeat() * reservationRequest.getTotalSeat());
        reservation.setPaid(false);
        return reservation;
    }
    
    public static List<ReservationSummary> setReservationResponse(List<Reservation> reservationList) {
        return reservationList.stream().map(ReservationMapper::setReservationSummary).collect(Collectors.toList());
    }
    
    private static ReservationSummary setReservationSummary(Reservation reservation) {
        ReservationSummary summary = new ReservationSummary();
        summary.setReservationDate(reservation.getReservationDate());
        summary.setKids(reservation.getKids());
        summary.setAdults(reservation.getAdults());
        summary.setSeniours(reservation.getSeniour());
        return summary;
    }
    
    public static ReservationResponse setReservationResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setTravelMode(reservation.getTravelMode());
        response.setFromCity(reservation.getFromCity());
        response.setToCity(reservation.getToCity());
        response.setDepartureDate(reservation.getDepartureDate());
        response.setReturnDate(reservation.getReturnDate());
        response.setDepartureTime(reservation.getDepartureTime());
        response.setReturnTime(reservation.getReturnTime());
        response.setKids(reservation.getKids());
        response.setAdults(reservation.getAdults());
        response.setSeniour(reservation.getSeniour());
        response.setReservationDate(reservation.getReservationDate());
        response.setTotalPrice(reservation.getTotalPrice());
        response.setPaid(reservation.isPaid());
        return response;
    }
}
