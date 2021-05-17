package com.cotiviti.repository;

import com.cotiviti.entities.UserGroup;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupRepository extends AbstractRepository<UserGroup, Long> {

    @Query("select g.name from #{#entityName} g where g.name in :names")
    List<String> getAllUserGroupByNames(@Param("names") List<String> list);

    UserGroup getByName(String name);
}
