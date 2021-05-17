package com.cotiviti.response;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationResponse {

    private String travelMode;
    private String fromCity;
    private String toCity;
    private Date departureDate;
    private Date returnDate;
    private Integer departureTime;
    private Integer returnTime;
    private int kids;
    private int adults;
    private int seniour;
    private Date reservationDate;
    private Double totalPrice;
    private boolean isPaid;
}
