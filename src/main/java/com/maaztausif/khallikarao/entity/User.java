package com.maaztausif.khallikarao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String email;
    private String fullName;
    private String password;
    //OTP
    private boolean emailVerified = false;
    private String otpHash;
    private java.time.Instant otpExpiresAt;
    private int otpAttempts = 0;
}
