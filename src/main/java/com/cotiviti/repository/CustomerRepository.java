package com.cotiviti.repository;

import com.cotiviti.entities.Customer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends AbstractRepository<Customer, Long> {

    @Query("Select c.username from #{#entityName} c where c.username in :usernames")
    List<String> getAllUsernames(@Param("usernames") List<String> usernames);

    Optional<Customer> findByUsername(String username);
}
