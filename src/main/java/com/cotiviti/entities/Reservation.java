package com.cotiviti.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reservation")
public class Reservation extends AbstractEntity {

    @Column(name = "travel_mode", nullable = false)
    private String travelMode;

    @Column(name = "from_city", nullable = false)
    private String fromCity;

    @Column(name = "to_city", nullable = false)
    private String toCity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "departure_date", nullable = false)
    private Date departureDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "return_date")
    private Date returnDate;

    @Column(name = "departure_time", nullable = false)
    private Integer departureTime;

    @Column(name = "return_time")
    private Integer returnTime;

    @Column(name = "kids", nullable = false)
    private int kids;

    @Column(name = "adults", nullable = false)
    private int adults;

    @Column(name = "seniour", nullable = false)
    private int seniour;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reservation_date", nullable = false)
    private Date reservationDate;

    @JoinColumn(name = "bus_details", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private BusDetails busDetails;

    @JoinColumn(name = "customer", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Customer customer;
}
