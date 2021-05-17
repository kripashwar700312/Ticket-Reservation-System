package com.cotiviti.request;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequest {

    @NotBlank(message = "Travel Mode cannot be blank")
    private String travelMode;

    @NotBlank(message = "From City cannot be blank")
    private String fromCity;

    @NotBlank(message = "To City cannot be blank")
    private String toCity;

    @NotNull(message = "Departure Date cannot be blank")
    private Date departureDate;

    private Date returnDate;

    @NotNull(message = "Departure Time cannot null")
    private Integer departureTime;

    private Integer returnTime;

    private int kids;

    private int adults;

    private int seniour;

    public int getTotalSeat() {
        return kids + adults + seniour;
    }
}
