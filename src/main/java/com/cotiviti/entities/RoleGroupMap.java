package com.cotiviti.entities;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "role_group_map")
public class RoleGroupMap extends AbstractEntity {

    @JoinColumn(name = "user_group", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserGroup userGroup;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @JoinColumn(name = "roles", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Role role;

}
