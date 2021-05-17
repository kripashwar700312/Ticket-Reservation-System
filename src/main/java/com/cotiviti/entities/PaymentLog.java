package com.cotiviti.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payment_log")
public class PaymentLog extends AbstractEntity {

    @Column(name = "payment_date", nullable = false)
    private Date paymentDate;

    @Column(name = "payment_amount", nullable = false)
    private Double paidAmount;

    @JoinColumn(name = "customer", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false)
    private Reservation reservation;
}
