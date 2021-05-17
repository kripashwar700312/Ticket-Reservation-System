package com.cotiviti.repository;

import com.cotiviti.entities.BusDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BusDetailsRepository extends AbstractRepository<BusDetails, Long> {

    @Query("select bd from #{#entityName} bd")
    BusDetails getBusDetails();
}
