package com.maaztausif.khallikarao.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class SignupResponse {
    private boolean status;
    private String message;
    private Boolean otpVerified;
    private UserData user;

    @Data
    @AllArgsConstructor
    public static class UserData {
        private long id;
        private String email;
        private String fullName;
    }
}
