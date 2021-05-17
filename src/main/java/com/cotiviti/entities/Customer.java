package com.cotiviti.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer extends AbstractEntity {

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "middleName")
    private String middleName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "password")
    private String password;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "gender")
    private String gender;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "wrong_password_attempt_count")
    private Integer wrongPasswordAttemptCount;

    @Column(name = "is_login_expired", nullable = false)
    private boolean isLoginExpired;

    @JoinColumn(name = "status", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Status status;

    @JoinColumn(name = "user_group", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private UserGroup userGroup;

    public String getFullName() {
        return firstName.trim() + " " + (middleName != null ? (middleName.trim() + " ") : "") + lastName.trim();
    }
}
