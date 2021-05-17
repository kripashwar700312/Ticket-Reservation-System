package com.cotiviti.response;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationSummary {

    private Date reservationDate;
    private int kids;
    private int adults;
    private int seniours;
}
