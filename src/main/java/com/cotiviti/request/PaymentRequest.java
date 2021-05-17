package com.cotiviti.request;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest extends JwtAuthenticationRequest {

    @NotNull(message = "reservation id cannot be empty")
    private Long reservationId;
}
