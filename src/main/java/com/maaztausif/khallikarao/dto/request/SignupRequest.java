package com.maaztausif.khallikarao.dto.request;

import lombok.Data;

@Data
public class SignupRequest {
    private String fullName;
    private String emailAddress;
    private String password;

}
