package com.cotiviti.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bus_details")
public class BusDetails extends AbstractEntity {

    @Column(name = "bus_number", nullable = false)
    private String busNumber;

    @Column(name = "seat_capacity", nullable = false)
    private int seatCapacity;

    @Column(name = "reserved_seat", nullable = false)
    private int reservedSeat;

    @Column(name = "available_seat", nullable = false)
    private int availableSeat;

    @Column(name = "price_per_seat", nullable = false)
    private Double pricePerSeat;

}
