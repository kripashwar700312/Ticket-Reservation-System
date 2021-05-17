package com.cotiviti.repository;

import com.cotiviti.entities.Reservation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends AbstractRepository<Reservation, Long> {

    @Query("select r from #{#entityName} r where r.customer.id =:customerId")
    List<Reservation> getAllReservation(@Param("customerId") Long customerId);

    @Query("select r from #{#entityName} r where r.id =:id and r.customer.id =:customerId")
    Optional<Reservation> getByIdAndCustomerId(@Param("id") Long id, @Param("customerId") Long customerId);

}
