package com.cotiviti.mapper;

import com.cotiviti.entities.PaymentLog;
import com.cotiviti.entities.Reservation;
import java.util.Date;

public class PaymentLogMapper {

    public static PaymentLog setPaymentLog(Reservation reservation) {
        PaymentLog paymentLog = new PaymentLog();
        paymentLog.setReservation(reservation);
        paymentLog.setPaymentDate(new Date());
        paymentLog.setPaidAmount(reservation.getTotalPrice());
        return paymentLog;
    }
}
