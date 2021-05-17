package com.cotiviti.repository;

import com.cotiviti.entities.RoleGroupMap;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleGroupMapRepository extends AbstractRepository<RoleGroupMap, Long> {

    @Query("select rgm.role.permission from #{#entityName} rgm where rgm.userGroup.name =:groupName")
    List<String> getRolePermissionsByGroupName(@Param("groupName") String groupName);
}
