package com.cotiviti.repository;

import com.cotiviti.entities.Role;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends AbstractRepository<Role, Long> {

    @Query("select r.name from #{#entityName} r where r.name in :names")
    List<String> getAllRoleByNames(@Param("names") List<String> list);

    Role getByName(String name);
}
