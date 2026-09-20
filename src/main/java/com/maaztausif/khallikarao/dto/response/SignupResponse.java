package com.maaztausif.khallikarao.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupResponse {
    private long id;
    private String email;
    private String fullName;
    private String password;
}
