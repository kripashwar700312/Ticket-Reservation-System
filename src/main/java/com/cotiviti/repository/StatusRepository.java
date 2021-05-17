package com.cotiviti.repository;

import com.cotiviti.entities.Status;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends AbstractRepository<Status, Long> {

    @Query("select s.name from #{#entityName} s where s.name in :names")
    List<String> getAllStatusByNames(@Param("names") List<String> list);

    Status getByName(String name);
}
